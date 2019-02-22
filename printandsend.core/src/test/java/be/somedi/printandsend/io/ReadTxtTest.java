package be.somedi.printandsend.io;

import be.somedi.printandsend.model.UMFormat;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReadTxtTest {

    private ReadTxt readTxt;

    @Before
    public void setup() {
        Path path = Paths.get("src/test/resources/MSE_183030005_2976737_A9671.txt");
        readTxt = new ReadTxt(path);
    }

    @Test
    public void getTextAfterKey() {
        String result = readTxt.getTextAfterKey("DR");
        assertEquals("A9671", result);

        result = readTxt.getTextAfterKey("PS");
        assertEquals("BOSSESTRAAT 5", result);
    }


    @Test
    public void getBody() {
        String result = readTxt.getBodyOfTxt(UMFormat.MEDIDOC);
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
//        assertEquals(StringUtils.deleteWhitespace(expected), StringUtils.deleteWhitespace(result));
    }

    @Test
    public void getFileName() {
        String actual = readTxt.getFileName();
        String expected = "MSE_183030005_2976737_A9671.txt";
        assertEquals(expected, actual);
    }

    @Test
    public void containsVulAan() {
        Path path = Paths.get("src/test/resources/vul_aan.txt");
        readTxt = new ReadTxt(path);
        assertTrue(readTxt.containsVulAan());
    }

    @Test
    public void containsSentenceToDelete() throws IOException {
        Path path = Paths.get("src/test/resources/geenverslag.txt");
        readTxt = new ReadTxt(path);
        assertTrue(readTxt.containsSentenceToDelete());
    }

    @Test
    public void testCopyAndDelete() throws IOException {
        Path newPath = Paths.get("src/test/resources/copy.txt");
        readTxt.moveTxtFile(newPath);
        assertTrue(Files.exists(newPath));
        Files.delete(newPath);
        assertFalse(Files.exists(newPath));
    }
}