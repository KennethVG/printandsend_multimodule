package be.somedi.printandsend.io;

import be.somedi.printandsend.model.ExternalCaregiver;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.lang3.StringUtils.left;

@Component
public class UMWriter {

    // TODO: WRITE TO ANSI --> Now UTF-8
    public void write(Path pathToUM, String output, ExternalCaregiver caregiverTo, String refNr) {
        try {
            // Make REP file
            Files.write(Paths.get(pathToUM + "\\HEC_" + caregiverTo.getExternalID() + "R_" + refNr + ".REP"), output.getBytes(Charset.forName("windows-1252")));

            // Make ADR file
            String first8NumbersOfRizivFromCaregiverTo = left(caregiverTo.getNihiiAddress() == null || caregiverTo.getNihiiAddress().equalsIgnoreCase("NULL") ? caregiverTo.getNihii() : caregiverTo.getNihiiAddress(), 8);
            Files.write(Paths.get(pathToUM + "\\HEC_" + caregiverTo.getExternalID() + "R_" + refNr + ".ADR"), first8NumbersOfRizivFromCaregiverTo.getBytes(Charset.forName("windows-1252")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
