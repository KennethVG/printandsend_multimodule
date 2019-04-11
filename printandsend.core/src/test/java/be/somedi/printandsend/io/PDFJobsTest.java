package be.somedi.printandsend.io;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class PDFJobsTest {

    private Path path;
    private PDFJobs PDFJobs;

    @Before
    public void init() {
        path = Paths.get("src/test/resources/MSE_183030005_2976737_A9671.txt");
        TXTJobs TXTJobs = new TXTJobs(path);
        PDFJobs = new PDFJobs(TXTJobs);
    }

    @Test
    public void getFileNameOfPDFToPrint() {
        String fileNameOfPDFToPrint = PDFJobs.getFileNameOfPDFToPrint();
        assertEquals("PDF_183030005_2976737_A9671.pdf", fileNameOfPDFToPrint);

        Path pathOfPDF = PDFJobs.getPathOfPDFToPrint();
        assertEquals(path.getParent(), pathOfPDF.getParent());
    }

    @Test
    public void getPathOfPDFToPrint() {
        Path pathOfPDF = Paths.get("src/test/resources/PDF_183030005_2976737_A9671.pdf");
        assertEquals(PDFJobs.getPathOfPDFToPrint(), pathOfPDF);
    }
}