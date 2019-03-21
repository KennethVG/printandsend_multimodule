package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.io.PrintPDF;
import be.somedi.printandsend.io.ReadTxt;
import be.somedi.printandsend.model.UMFormat;
import be.somedi.printandsend.service.ExternalCaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executor;
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
            while (true) {
                final WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    final Path txtFile = (Path) event.context();
                    if (txtFile.toString().startsWith("MSE") && txtFile.toString().endsWith(".txt")) {
                        printForExternalCaregiver(Paths.get(pathNew + "\\" + txtFile));
                    }
                }
                if (!key.reset()) {
                    System.out.println("Key had been unregistered");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopPrintJob() {
        try {
            watchService.close();
            future.cancel(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processEventsBeforeWatching() {
        try {
            Files.list(pathNew).forEach(txtFile -> {
                if (txtFile.getFileName().toString().startsWith("MSE") && txtFile.toString().endsWith(".txt")) {
                    try {
                        printForExternalCaregiver(txtFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printForExternalCaregiver(Path txtFile) throws IOException {
        ReadTxt readTxt = new ReadTxt(txtFile);
        PrintPDF printPDF = new PrintPDF(readTxt);

        if (readTxt.containsVulAan()) {
            printPDF.copyAndDeleteTxtAndPDF(pathError);
        } else if (readTxt.containsSentenceToDelete()) {
            printPDF.deleteTxtAndPDF();
        } else {
            String externalIdOfCaregiver = readTxt.getExternalId();
            if (externalIdOfCaregiver != null) {
                ExternalCaregiverEntity externalCaregiverEntity = externalCaregiverService.findByExternalID(externalIdOfCaregiver);
                if (externalCaregiverEntity != null) {
                    Boolean needEPrint = externalCaregiverEntity.geteProtocols();
                    if (needEPrint) {
                        sendToUM(readTxt, externalCaregiverEntity.getFormat());
                    }

                    Boolean needPrint = externalCaregiverEntity.getPrintProtocols();
                    Boolean needSecondCopy = externalCaregiverEntity.getSecondCopy();
                    if (needPrint == null || needPrint.toString().equals("")) {
                        //TODO: Errorhandling!
                        printPDF.copyAndDeleteTxtAndPDF(pathError);
                    } else if (needPrint) {
                        printPDF.printPDF();
                        if (needSecondCopy != null && !needSecondCopy.toString().equals("") && needSecondCopy) {
                            printPDF.printPDF();
                        }
                        printPDF.copyAndDeleteTxtAndPDF(pathResult);
                    } else {
                        printPDF.copyAndDeleteTxtAndPDF(pathResult);
                    }
                }
            } else {
                //TODO:errorhandling --> Geen geldig externalId
            }
        }
    }

    private void sendToUM(ReadTxt readTxt, UMFormat umFormat) {
        switch (umFormat) {
            case MEDIDOC:
                createUMFormat.createMedidocFile(readTxt);
                break;
            case MEDAR:
                createUMFormat.createMedarFile(readTxt);
                break;
            case MEDICARD:
                createUMFormat.createMedicardFile(readTxt);
                break;
        }
    }
}
