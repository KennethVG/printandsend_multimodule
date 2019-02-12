package be.somedi.printandsend.io;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
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
    public void getBody() throws IOException {
        String result = readTxt.getBodyOfTxt();
        System.out.println(result);
    }
}