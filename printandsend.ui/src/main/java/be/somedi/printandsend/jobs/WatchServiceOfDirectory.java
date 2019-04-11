package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.exceptions.CaregiverNotFoundException;
import be.somedi.printandsend.io.PDFJobs;
import be.somedi.printandsend.io.TXTJobs;
import be.somedi.printandsend.model.UMFormat;
import be.somedi.printandsend.service.ExternalCaregiverService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
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
            Files.list(pathNew).forEach(txtFile -> {
                if (txtFile.getFileName().toString().startsWith("MSE") && txtFile.toString().endsWith(".txt")) {
                    try {
                        printForExternalCaregiver(txtFile);
                    } catch (IOException e) {
                        LOGGER.error("Kan de error file niet aanmaken");
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void printForExternalCaregiver(Path txtFile) throws IOException {
        String fileName = txtFile.getFileName().toString();
        TXTJobs TXTJobs = new TXTJobs(txtFile);
        PDFJobs PDFJobs = new PDFJobs(TXTJobs);

        if (TXTJobs.containsVulAan()) {
            LOGGER.info(fileName + " bevat vul_aan in de tekst.");
            PDFJobs.copyAndDeleteTxtAndPDF(pathError);
            Files.write(Paths.get(pathError + "\\" + fileName + ".err"), "Ergens in de tekst zit nog het woord vul_aan".getBytes());
        } else if (TXTJobs.containsSentenceToDelete()) {
            LOGGER.info(fileName + " mag verwijderd worden.");
            PDFJobs.deleteTxtAndPDF();
        } else {
            LOGGER.info(fileName + " wordt verwerkt...");
            String externalIdOfCaregiver = TXTJobs.getExternalId();
            if (externalIdOfCaregiver != null) {
                ExternalCaregiverEntity externalCaregiverEntity = externalCaregiverService.findByExternalID(externalIdOfCaregiver);
                LOGGER.info(externalCaregiverEntity);
                if (externalCaregiverEntity != null) {
                    String aanspreking = "Dr. " + externalCaregiverEntity.getLastName();
                    Boolean needEPrint = externalCaregiverEntity.geteProtocols();
                    if (needEPrint != null && needEPrint) {
                        LOGGER.info(aanspreking + " wil graag een elektronische versie ontvangen in formaat: " + externalCaregiverEntity.getFormat());
                        sendToUM(TXTJobs, externalCaregiverEntity.getFormat());
                    }

                    Boolean needPrint = externalCaregiverEntity.getPrintProtocols();
                    Boolean needSecondCopy = externalCaregiverEntity.getSecondCopy();
                    if (needPrint == null || needPrint.toString().equals("")) {
                        PDFJobs.copyAndDeleteTxtAndPDF(pathError);
                    } else if (needPrint) {
                        LOGGER.info(aanspreking + " wil graag een papieren versie ontvangen.");
                        PDFJobs.printPDF();
                        if (needSecondCopy != null && !needSecondCopy.toString().equals("") && needSecondCopy) {
                            LOGGER.info(aanspreking + " wil graag nog een papieren versie ontvangen.");
                            PDFJobs.printPDF();
                        }
                        PDFJobs.copyAndDeleteTxtAndPDF(pathResult);
                    } else {
                        PDFJobs.copyAndDeleteTxtAndPDF(pathResult);
                    }
                }
            } else {
                String errorMessagge = "ExternalId niet gevonden. Je kan het externalId terugvinden in de txt helemaal bovenaan na het keyword #DR: ";
                LOGGER.error(errorMessagge);
                PDFJobs.copyAndDeleteTxtAndPDF(pathError);
                Files.write(Paths.get(pathError + "\\" + fileName + ".err"), errorMessagge.getBytes());
                throw new CaregiverNotFoundException(errorMessagge);
            }
        }
    }

    private void sendToUM(TXTJobs TXTJobs, UMFormat umFormat) {
        switch (umFormat) {
            case MEDIDOC:
                createUMFormat.createMedidocFile(TXTJobs);
                LOGGER.info("Er is succesvol een MEDIDOC file aangemaakt!");
                break;
            case MEDAR:
                createUMFormat.createMedarFile(TXTJobs);
                LOGGER.info("Er is succesvol een MEDAR file aangemaakt!");
                break;
            case MEDICARD:
                createUMFormat.createMedicardFile(TXTJobs);
                LOGGER.info("Er is succesvol een MEDICARD file aangemaakt!");
                break;
        }
    }
}
