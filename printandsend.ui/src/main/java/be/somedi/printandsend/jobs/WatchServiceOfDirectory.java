package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.io.PrintPDF;
import be.somedi.printandsend.io.ReadTxt;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.service.ExternalCaregiverService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class WatchServiceOfDirectory {

    @Value("${path-new}")
    private Path pathNew;
    @Value("${path-result}")
    private Path pathResult;
    @Value("${path-error}")
    private Path pathError;

    private final ExternalCaregiverService externalCaregiverService;

    @Autowired
    public WatchServiceOfDirectory(ExternalCaregiverService externalCaregiverService) {
        this.externalCaregiverService = externalCaregiverService;
    }

    public void processEvents() throws IOException, PrinterException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
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
    }

    public void processEventsBeforeWatching() throws IOException {
        Files.list(pathNew).forEach(txtFile -> {
            try {
                if (txtFile.getFileName().toString().startsWith("MSE") && txtFile.toString().endsWith(".txt")) {
                    printForExternalCaregiver(txtFile);
                }
            } catch (PrinterException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void printForExternalCaregiver(Path txtFile) throws PrinterException, IOException {
        ReadTxt readTxt = new ReadTxt(txtFile);
        PrintPDF printPDF = new PrintPDF(readTxt);

        if (readTxt.containsVulAan()) {
            printPDF.copyAndDeleteTxtAndPDF(pathError);
        } else if (readTxt.containsSentenceToDelete()) {
            System.out.println("Bevat P.N. ... --> Direct verwijderen");
            printPDF.deleteTxtAndPDF();
        } else {
            String externalIdOfCaregiver = readTxt.getTextAfterKey("DR");
            ExternalCaregiverEntity externalCaregiverEntity = externalCaregiverService.findByExternalID(externalIdOfCaregiver);
            if (externalCaregiverEntity != null) {
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
        }
    }
}
