package be.somedi.printandsend.io;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PDFJobs {

    private static final Logger LOGGER = LogManager.getLogger(PDFJobs.class);

    private final TXTJobs txtJobs;

    public PDFJobs(TXTJobs txtJobs) {
        this.txtJobs = txtJobs;
    }

    String getFileNameOfPDFToPrint() {
        String txtToPDF = txtJobs.getFileName().replace("MSE", "PDF").replace("txt", "pdf");
        Path toPrint = Paths.get(txtToPDF);
        return toPrint.getFileName().toString();
    }

    Path getPathOfPDFToPrint() {
        Path read = txtJobs.getPath();
        return Paths.get(read.getParent() + "\\" + getFileNameOfPDFToPrint());
    }

    public void copyAndDeleteTxtAndPDF(Path pathToMove) {
        try {
            Files.copy(getPathOfPDFToPrint(), Paths.get(pathToMove + "\\" + getFileNameOfPDFToPrint()), StandardCopyOption.REPLACE_EXISTING);
            txtJobs.moveTxtFile(Paths.get(pathToMove + "\\" + txtJobs.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            deleteTxtAndPDF();
        }
    }

    public void deleteTxtAndPDF() {
        if (FileUtils.deleteQuietly(getPathOfPDFToPrint().toFile())) {
            LOGGER.info(getPathOfPDFToPrint() + " succesvol verwijderd");
        }
        if (txtJobs.deleteTxtFile()) {
            LOGGER.info(txtJobs.getPath() + " succesvol verwijderd");
        }
    }

    public void printPDF() {
        try {
            Path pathOfPDFToPrint = getPathOfPDFToPrint();
            try (PDDocument doc = PDDocument.load(pathOfPDFToPrint.toFile())) {
                LOGGER.info("Aan het printen...");
                PrinterJob printerJob = PrinterJob.getPrinterJob();
                printerJob.setPageable(new PDFPageable(doc));
                PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
                printerJob.setPrintService(defaultPrintService);
                printerJob.print();
                LOGGER.info(pathOfPDFToPrint + " is uitgeprint");
            }
        } catch (PrinterException e) {
            LOGGER.error("Default printer not available");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
