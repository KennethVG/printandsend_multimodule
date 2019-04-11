package be.somedi.printandsend.io;

import be.somedi.printandsend.model.UMFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TXTJobsTest {

    private TXTJobs TXTJobs;

    @Before
    public void setup() {
        Path path = Paths.get("src/test/resources/MSE_183030005_2976737_A9671.txt");
        TXTJobs = new TXTJobs(path);
    }

    @Test
    public void getTextAfterKey() {
        String result = TXTJobs.getTextAfterKey("DR");
        assertEquals("A9671", result);

        result = TXTJobs.getTextAfterKey("PS");
        assertEquals("BOSSESTRAAT 5", result);
    }


    @Test
    public void getBody() {
        String result = TXTJobs.getBodyOfTxt(UMFormat.MEDIDOC);
        String expected = "Betreft : uw patiënt(e) Van Acker Sylvie geboren op 14/09/1982   en\r\n" +
                " wonende \r\n" +
                "BOSSESTRAAT 5 te 2220 Heist-op-den-Berg. \r\n" +
                "\r\n" +
                " Consultatie : 09/02/2018 met referentienr : 183030005 \r\n" +
                "\r\n" +
                "Dit is een test van Kenneth Van Gijsel.\r\n" +
                "Dit is een hele lange zin, langer dan 75 karakters en dat mag niet in\r\n" +
                " Medidoc formaat. Dit moet gesplits worden.\r\n" +
                "En dan liefst dat dit mee op de vorige zin komt. En nu gaan we het nog een\r\n" +
                " beetje moeilijker maken. Door nog een zin\r\n" +
                "toe te voegen die veel te lang is. Is zien hoe het programma hier mee\r\n" +
                " omgaat!\r\n" +
                "Werkt niet ...\r\n" +
                "\r\n" +
                "BESLUIT:\r\n" +
                "]Mijn besluit regel 1.\r\n" +
                "]Mijn besluit regel 2.\r\n" +
                "]Mijn besluit regel 3.\r\n" +
                "]Mijn besluit regel 4.\r\n" +
                "]Mijn besluit regel 5.\r\n" +
                "]Mijn besluit regel 6.\r\n" +
                "Mijn besluit regel 7.\r\n" +
                "Mijn besluit regel 8.";

        assertEquals(expected, result);
    }

    @Test
    public void getFileName() {
        String actual = TXTJobs.getFileName();
        String expected = "MSE_183030005_2976737_A9671.txt";
        assertEquals(expected, actual);
    }

    @Test
    public void containsVulAan() {
        Path path = Paths.get("src/test/resources/vul_aan.txt");
        TXTJobs = new TXTJobs(path);
        assertTrue(TXTJobs.containsVulAan());
    }

    @Test
    public void containsSentenceToDelete() throws IOException {
        Path path = Paths.get("src/test/resources/geenverslag.txt");
        TXTJobs = new TXTJobs(path);
        assertTrue(TXTJobs.containsSentenceToDelete());
    }

    @Test
    public void testCopyAndDelete() throws IOException {
        Path newPath = Paths.get("src/test/resources/copy.txt");
        TXTJobs.moveTxtFile(newPath);
        assertTrue(Files.exists(newPath));
        Files.delete(newPath);
        assertFalse(Files.exists(newPath));
    }
}