package be.somedi.printandsend.io;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class PrintPDFTest {

    private Path path;
    private PrintPDF printPDF;

    @Before
    public void init() {
        path = Paths.get("src/test/resources/MSE_183030005_2976737_A9671.txt");
        ReadTxt readTxt = new ReadTxt(path);
        printPDF = new PrintPDF(readTxt);
    }

    @Test
    public void getFileNameOfPDFToPrint() {
        String fileNameOfPDFToPrint = printPDF.getFileNameOfPDFToPrint();
        assertEquals("PDF_183030005_2976737_A9671.pdf", fileNameOfPDFToPrint);

        Path pathOfPDF = printPDF.getPathOfPDFToPrint();
        assertEquals(path.getParent(), pathOfPDF.getParent());
    }

    @Test
    public void getPathOfPDFToPrint() {
        Path pathOfPDF = Paths.get("src/test/resources/PDF_183030005_2976737_A9671.pdf");
        assertEquals(printPDF.getPathOfPDFToPrint(), pathOfPDF);
    }
}