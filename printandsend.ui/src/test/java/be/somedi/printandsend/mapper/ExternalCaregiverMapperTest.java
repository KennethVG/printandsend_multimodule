package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.model.UMFormat;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExternalCaregiverMapperTest {

    @Test
    public void entityToExternalCaregiver() {
        ExternalCaregiver externalCaregiver = new ExternalCaregiver("A2617", "Jos", "Test", "123456789", "TestStraat 10",
                "2590", "Berlaar", "DR.", true, "016680097", true, UMFormat.MEDIDOC, "12345875421", true, false);
        ExternalCaregiverEntity externalCaregiverEntity = new ExternalCaregiverEntity("A2617", "Jos", "Test", "123456789", "TestStraat 10",
                "2590", "Berlaar", "DR.", "016680097", true, true, UMFormat.MEDIDOC, "12345875421", true, false);
        ExternalCaregiver copyOfEntity = ExternalCaregiverMapper.INSTANCE.entityToExternalCaregiver(externalCaregiverEntity);
        assertEquals(externalCaregiver, copyOfEntity);
    }
}