package be.somedi.printandsend.io;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class ReadTxtTest {

    @Test
    public void getToExternalCaregiver() {
    }

    @Test
    public void getFromExternalCaregiver() {
    }

    @Test
    public void getPatient() {
    }

    @Test
    public void getTextAfterKey() {
        URL url = getClass().getClassLoader().getResource("letter.txt");
        Path path = new File(url.getFile()).toPath();

        ReadTxt readTxt = new ReadTxt(path);

        String result = readTxt.getTextAfterKey("DR");
        assertEquals("A8393", result);

        result = readTxt.getTextAfterKey("PS");
        assertEquals("Tremelosesteenweg 134", result);
    }
}