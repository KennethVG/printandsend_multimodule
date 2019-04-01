package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.model.Patient;
import be.somedi.printandsend.model.Person;
import be.somedi.printandsend.model.UMFormat;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class PatientMapperTest {

    @Test
    public void patientEntityToPatient() {

        Person person = new Person("Jos", "De Bie", "88101322344", LocalDate.now());
        ExternalCaregiver externalCaregiver = new ExternalCaregiver("A2617", "Jos", "Test", "123456789", "TestStraat 10",
                "2590", "Berlaar", "DR.", true, "016680097", true, UMFormat.MEDIDOC, "12345875421", true, false);
        Patient patient = new Patient("M123456", person, externalCaregiver);

        PatientEntity patientEntity = PatientMapper.INSTANCE.patientToPatientEntity(patient);

        assertEquals(patient.getExternalId(), patientEntity.getExternalId());
        assertEquals(patient.getPerson(), patient.getPerson());

    }
}