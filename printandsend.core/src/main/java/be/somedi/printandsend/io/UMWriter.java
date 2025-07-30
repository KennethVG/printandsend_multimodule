package be.somedi.printandsend.io;

import be.somedi.printandsend.model.ExternalCaregiver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.lang3.StringUtils.left;

@Component
public class UMWriter {

    public void write(Path pathToUM, String output, ExternalCaregiver caregiverTo, String refNr) {
        try {
            // Make REP file
            Files.write(Paths.get(pathToUM + "\\HEC_" + caregiverTo.getExternalID() + "R_" + refNr + "R.REP"), output.getBytes(Charset.forName("windows-1252")));

            // Make ADR file
            String first8NumbersOfRizivFromCaregiverTo = left(caregiverTo.getInstitutionNumber() == null || caregiverTo.getInstitutionNumber().equalsIgnoreCase("NULL") ? caregiverTo.getNihii() : caregiverTo.getInstitutionNumber(), 8);
            Files.write(Paths.get(pathToUM + "\\HEC_" + caregiverTo.getExternalID() + "R_" + refNr + "R.ADR"), first8NumbersOfRizivFromCaregiverTo.getBytes(Charset.forName("windows-1252")));

            //TODO: error handling
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
