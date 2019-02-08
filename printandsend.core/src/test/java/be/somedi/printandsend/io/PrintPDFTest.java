package be.somedi.printandsend.io;

import org.junit.Test;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class PrintPDFTest {

    @Test
    public void getFileNameOfPDFToPrint() {
        URL url = getClass().getClassLoader().getResource("MSE_183030005_2976737_A9671.txt");
        Path path = new File(url.getFile()).toPath();
        ReadTxt readTxt = new ReadTxt(path);

        PrintPDF printPDF = new PrintPDF(readTxt);
        String fileNameOfPDFToPrint = printPDF.getFileNameOfPDFToPrint();
        assertEquals("PDF_183030005_2976737_A9671.pdf", fileNameOfPDFToPrint);

        Path pathOfPDF = printPDF.getPathOfPDFToPrint();
        assertEquals(path.getParent(), pathOfPDF.getParent());
    }
}