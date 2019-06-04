package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.io.PDFJobs;
import be.somedi.printandsend.io.TXTJobs;
import be.somedi.printandsend.service.ExternalCaregiverService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.Future;

@Component
public class WatchServiceOfDirectory {

    @Value("${path-new}")
    private Path pathNew;
    @Value("${path-result}")
    private Path pathResult;
    @Value("${path-error}")
    private Path pathError;

    private final ExternalCaregiverService externalCaregiverService;
    private final CreateUMFormat createUMFormat;
    private WatchService watchService;
    private final Future<WatchService> future;

    private static final Logger LOGGER = LogManager.getLogger(WatchServiceOfDirectory.class);

    @Autowired
    public WatchServiceOfDirectory(ExternalCaregiverService externalCaregiverService, CreateUMFormat createUMFormat, WatchService watchService, Future<WatchService> future) {
        this.externalCaregiverService = externalCaregiverService;
        this.createUMFormat = createUMFormat;
        this.watchService = watchService;
        this.future = future;
    }

    @Async("threadPoolTaskExecutor")
    public void processEvents() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            pathNew.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            boolean valid = true;
            while (valid) {

                final WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    final Path txtFile = (Path) event.context();
                    if (txtFile.toString().startsWith("MSE") && txtFile.toString().endsWith(".txt")) {
                        printForExternalCaregiver(Paths.get(pathNew + "\\" + txtFile));
                    }
                }
                valid = key.reset();
                if (!valid) {
                    LOGGER.error("Key had been unregistered");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("De service is gestopt.");
        } catch (ClosedWatchServiceException e) {
            LOGGER.warn("De service is gestopt.");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(LOGGER::error);
        }
    }

    public void stopPrintJob() {
        try {
            watchService.close();
            Thread.currentThread().interrupt();
            future.cancel(true);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void processEventsBeforeWatching() {
        try {
            LOGGER.info("Path om te lezen: " + pathNew);
            Files.list(pathNew).forEach(file -> {
                if (file.getFileName().toString().startsWith("MSE") && file.toString().endsWith(".txt")) {
                    printForExternalCaregiver(file);
                }
            });
            // Verwijder resterende PDF files uit new folder.
            Files.list(pathNew).filter(path -> FilenameUtils.getExtension(path.getFileName().toString()).equalsIgnoreCase("PDF")).forEach(path -> {
                LOGGER.info(path + " succesvol verwijderd (pdf blijven staan in folder new)");
                FileUtils.deleteQuietly(path.toFile());
            });

            // Opnieuw versturen van de files in error folder.
            Files.list(pathError).filter(path -> FilenameUtils.getExtension(path.getFileName().toString()).equalsIgnoreCase("err")).forEach(path -> FileUtils.deleteQuietly(path.toFile()));

            LOGGER.info("Proberen om errors nogmaals te verwerken");
            FileSystemUtils.copyRecursively(pathError, pathNew);
            LOGGER.info("Alle files uit error folder verwijderen");
            FileUtils.cleanDirectory(pathError.toFile());

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void printForExternalCaregiver(Path txtFile) {
        String fileName = txtFile.getFileName().toString();
        TXTJobs txtJobs = new TXTJobs(txtFile);
        PDFJobs pdfJobs = new PDFJobs(txtJobs);
        String errorMessage;


        if (fileName.contains("EMD")) {
            LOGGER.info(fileName + " EMD ... dus mag verwijderd worden. Geen geldig externalID");
            pdfJobs.deleteTxtAndPDF();
        } else if (txtJobs.containsSentenceToDelete()) {
            LOGGER.info(fileName + " bevat P.N., mag weg ... dus mag verwijderd worden.");
            pdfJobs.deleteTxtAndPDF();
        } else if (txtJobs.containsVulAan()) {
            errorMessage = "Ergens in de tekst zit nog het woord vul_aan";
            makeErrorMessage(errorMessage, pdfJobs, fileName);
        } else {
            LOGGER.info(fileName + " wordt verwerkt...");
            String result = createUMFormat.sendToUM(txtJobs);
            switch (result) {
                case CreateUMFormat.SPECIALIST_ONBEKEND:
                    errorMessage = "Specialist Somedi is onbekend!";
                    makeErrorMessage(errorMessage, pdfJobs, fileName);
                    break;
                case CreateUMFormat.LEGE_BODY:
                    if (txtJobs.getIndex("betreft") == 0 || txtJobs.getIndex("geachte") == 0) {
                        errorMessage = "De TXT bevat geen begin (BETREFT/ GEACHTE)";
                    } else {
                        errorMessage = "De TXT bevat geen einde (Met vriendelijke groeten/ Met collegiale groeten)";
                    }
                    makeErrorMessage(errorMessage, pdfJobs, fileName);
                    break;
                default:
                    String externalIdOfCaregiver = txtJobs.getExternalIdOfCaregiverTo();
                    String externalIdOfCaregiverFrom = txtJobs.getExternalIdOfCaregiverFrom();
                    if (externalIdOfCaregiver != null) {
                        ExternalCaregiverEntity externalCaregiverEntity = externalCaregiverService.findByExternalID(externalIdOfCaregiver);
                        if (externalCaregiverEntity != null) {
                            String aanspreking = "Dr. " + externalCaregiverEntity.getLastName();
                            Boolean needPrint = externalCaregiverEntity.getPrintProtocols();
                            if (externalIdOfCaregiverFrom != null) {
                                ExternalCaregiverEntity externalCaregiverEntityFrom = externalCaregiverService.findByExternalID(externalIdOfCaregiverFrom);
                                Boolean needSecondCopy = externalCaregiverEntityFrom.getSecondCopy();
                                if (needSecondCopy != null && !needSecondCopy.toString().equals("") && needSecondCopy) {
                                    LOGGER.info(externalCaregiverEntityFrom.getLastName() + " wil graag een kopie van de brief ontvangen.");
                                    pdfJobs.printPDF();
                                }
                            }
                            if (needPrint == null || needPrint.toString().equals("")) {
                                errorMessage = "Printprotocols is NULL of LEEG. Zet deze op true of false voor Dr. met externalID: " + externalIdOfCaregiver;
                                makeErrorMessage(errorMessage, pdfJobs, fileName);
                            } else if (needPrint) {
                                LOGGER.info(aanspreking + " wil graag een papieren versie ontvangen.");
                                pdfJobs.printPDF();
                                pdfJobs.copyAndDeleteTxtAndPDF(pathResult);
                            } else {
                                LOGGER.info(aanspreking + " wenst geen papieren versie");
                                pdfJobs.copyAndDeleteTxtAndPDF(pathResult);
                            }
                        } else {
                            errorMessage = "Caregiver met externalId " + externalIdOfCaregiver + " niet gevonden.";
                            makeErrorMessage(errorMessage, pdfJobs, fileName);
                        }
                    } else {
                        errorMessage = "ExternalId niet gevonden. Je kan het externalId terugvinden in de txt helemaal bovenaan na het keyword #DR: ";
                        makeErrorMessage(errorMessage, pdfJobs, fileName);
                    }
                    break;
            }
        }
    }


    private void makeErrorMessage(String errorMessage, PDFJobs pdfJobs, String fileName) {
        LOGGER.error(errorMessage);
        pdfJobs.copyAndDeleteTxtAndPDF(pathError);
        try {
            Files.write(Paths.get(pathError + "\\" + FilenameUtils.getBaseName(fileName) + ".err"), errorMessage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
