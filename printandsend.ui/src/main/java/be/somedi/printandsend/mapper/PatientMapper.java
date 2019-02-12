package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    PatientEntity patientToPatientEntity(Patient patient);

    Patient patientEntityToPatient(PatientEntity patientEntity);
}