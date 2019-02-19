package be.somedi.printandsend.io;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class ReadTxtTest {

    private ReadTxt readTxt;

    @Before
    public void setup() {
        URL url = getClass().getClassLoader().getResource("MSE_183030005_2976737_A9671.txt");
        Path path = null;
        if (url != null) {
            path = new File(url.getFile()).toPath();
        }
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
        String result = readTxt.getBodyOfTxt();
        String expected = "\r\n" +
                "Betreft : uw patiënt(e) Van Acker Sylvie geboren op 14/09/1982   en wonende\n" +
                "BOSSESTRAAT 5 te 2220 Heist-op-den-Berg. \r\n" +
                "\r\n" +
                "Consultatie : 09/02/2018 met referentienr : 183030005\r\n" +
                "\r\n" +
                "Dit is een test van Kenneth Van Gijsel.\r\n" +
                "Dit is een hele lange zin, langer dan 75 karakters en dat mag niet in Medid\r\n" +
                "oc formaat. Dit moet gesplits worden. En dan liefst dat dit mee op de vorig\r\n" +
                "e zin komt. En nu gaan we het nog een beetje moeilijker maken. Door nog een\r\n" +
                " zin toe te voegen die veel te lang is. Is zien hoe het programma hier mee \r\n" +
                "omgaat! Werkt niet ... \r\n" +
                "\r\n" +
                "Besluit:\r\n" +
                "] Mijn besluit regel 1.\r\n" +
                "] Mijn besluit regel 2.\r\n" +
                "] Mijn besluit regel 3.\r\n" +
                "] Mijn besluit regel 4.\r\n" +
                "] Mijn besluit regel 5.\r\n" +
                "] Mijn besluit regel 6.\r\n" +
                "] Mijn besluit regel 7.\r\n" +
                "Mijn besluit regel 8.\r\n";

        assertEquals(StringUtils.deleteWhitespace(expected), StringUtils.deleteWhitespace(result));
    }
}