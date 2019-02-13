package be.somedi.printandsend.io;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.*;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterState;
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

    public void printPDF() throws PrinterException, IOException {
        PDDocument doc = PDDocument.load(getPathOfPDFToPrint().toFile());
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPageable(new PDFPageable(doc));
        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        PrintServiceAttributeSet printServiceAttributeSet = defaultPrintService.getAttributes();
        PrinterState printerState = (PrinterState) printServiceAttributeSet.get(PrinterState.class);

        if (printerState != null) {
            printerJob.setPrintService(defaultPrintService);
            printerJob.print();
        } else {
            //TODO: error handling
            System.out.println("Default printer niet online");
        }
        doc.close();
    }
}
