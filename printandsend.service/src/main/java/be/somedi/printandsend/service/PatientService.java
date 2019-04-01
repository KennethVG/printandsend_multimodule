package be.somedi.printandsend.service;

import be.somedi.printandsend.entity.PatientEntity;

import java.util.List;

public interface PatientService {

    PatientEntity findByExternalId(String externalId);

    List<PatientEntity> findByName(String name);

    Integer updatePatientEntity(String externalId, String doctorId);
}
