package be.somedi.printandsend.io;

import org.junit.Before;
import org.junit.Test;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PrintPDFTest {

    private ReadTxt readTxt;
    private Path path;
    private PrintPDF printPDF;

    @Before
    public void init() {
        URL url = getClass().getClassLoader().getResource("MSE_183030005_2976737_A9671.txt");
        if (url != null) {
            path = new File(url.getFile()).toPath();
        }
        readTxt = new ReadTxt(path);
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
    public void testCopyAndDelete() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        Path newPath = Paths.get(resourcesDirectory.toPath() + "\\copy.txt");
        readTxt.moveTxtFile(newPath);
        assertTrue(Files.exists(newPath));
        Files.delete(newPath);
        assertFalse(Files.exists(newPath));
    }
}