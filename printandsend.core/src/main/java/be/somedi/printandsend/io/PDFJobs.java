package be.somedi.printandsend.io;

import be.somedi.printandsend.exceptions.PathNotFoundException;
import be.somedi.printandsend.exceptions.PrinterNotFoundException;
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

    private TXTJobs TXTJobs;

    public PDFJobs(TXTJobs TXTJobs) {
        this.TXTJobs = TXTJobs;
    }

    public String getFileNameOfPDFToPrint() {
        String txtToPDF = TXTJobs.getFileName().replace("MSE", "PDF").replace("txt", "pdf");
        Path toPrint = Paths.get(txtToPDF);
        return toPrint.getFileName().toString();
    }

    public Path getPathOfPDFToPrint() {
        Path read = TXTJobs.getPath();
        return Paths.get(read.getParent() + "\\" + getFileNameOfPDFToPrint());
    }

    public void copyAndDeleteTxtAndPDF(Path pathToMove) {
        try {
            Files.copy(getPathOfPDFToPrint(), Paths.get(pathToMove + "\\" + getFileNameOfPDFToPrint()), StandardCopyOption.REPLACE_EXISTING);
            TXTJobs.moveTxtFile(Paths.get(pathToMove + "\\" + TXTJobs.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            deleteTxtAndPDF();
        }
    }

    public void deleteTxtAndPDF() {
        if (FileUtils.deleteQuietly(getPathOfPDFToPrint().toFile())) {
            LOGGER.info("PDF verwijderd");
        }
        if (TXTJobs.deleteTxtFile()) {
            LOGGER.info("TXT verwijderd");
        }
    }

    public void printPDF() {
        try {
            PDDocument doc = PDDocument.load(getPathOfPDFToPrint().toFile());
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPageable(new PDFPageable(doc));
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            printerJob.setPrintService(defaultPrintService);
//            printerJob.print();
            doc.close();
            LOGGER.info("PDF is uitgeprint");
        } catch (PrinterException e) {
            throw new PrinterNotFoundException("Default printer not available");
        } catch (IOException e) {
            throw new PathNotFoundException("Path niet gevonden");
        }
    }
}
