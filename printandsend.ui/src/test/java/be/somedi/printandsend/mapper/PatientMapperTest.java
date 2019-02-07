package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.model.Patient;
import be.somedi.printandsend.model.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class PatientMapperTest {

    @Test
    public void patientEntityToPatient() {

        Person person = new Person("Jos", "De Bie", "88101322344");
        Patient patient = new Patient("M123456", person);

        PatientEntity patientEntity = PatientMapper.INSTANCE.patientToPatientEntity(patient);

        assertEquals(patient.getExternalId(), patientEntity.getExternalId());
        assertEquals(patient.getPerson(), patient.getPerson());

    }
}