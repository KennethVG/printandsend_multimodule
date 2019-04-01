package be.somedi.printandsend.io;

import be.somedi.printandsend.exceptions.PathNotFoundException;
import be.somedi.printandsend.exceptions.PrinterNotFoundException;
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

public class PrintPDF {

    private ReadTxt readTxt;

    public PrintPDF(ReadTxt readTxt) {
        this.readTxt = readTxt;
    }

    public String getFileNameOfPDFToPrint() {
        String txtToPDF = readTxt.getFileName().replace("MSE", "PDF").replace("txt", "pdf");
        Path toPrint = Paths.get(txtToPDF);
        return toPrint.getFileName().toString();
    }

    public Path getPathOfPDFToPrint() {
        Path read = readTxt.getPath();
        return Paths.get(read.getParent() + "\\" + getFileNameOfPDFToPrint());
    }

    public void copyAndDeleteTxtAndPDF(Path pathToMove) throws IOException {
        Files.copy(getPathOfPDFToPrint(), Paths.get(pathToMove + "\\" + getFileNameOfPDFToPrint()), StandardCopyOption.REPLACE_EXISTING);
        Files.delete(getPathOfPDFToPrint());
        readTxt.moveTxtFile(Paths.get(pathToMove + "\\" + readTxt.getFileName()));
        readTxt.deleteTxtFile();
    }

    public void deleteTxtAndPDF() throws IOException {
        Files.delete(getPathOfPDFToPrint());
        readTxt.deleteTxtFile();
    }

    public void printPDF() {
        try {
            PDDocument doc = PDDocument.load(getPathOfPDFToPrint().toFile());
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPageable(new PDFPageable(doc));
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            printerJob.setPrintService(defaultPrintService);
            printerJob.print();
            System.out.println("Printing ...");
            doc.close();

            //TODO: error handling
        } catch (PrinterException e) {
            throw new PrinterNotFoundException("Default printer not available");
        } catch (IOException e) {
            throw new PathNotFoundException("Path niet gevonden");
        }
    }
}
