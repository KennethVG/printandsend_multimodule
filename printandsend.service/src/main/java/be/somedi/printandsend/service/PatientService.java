package be.somedi.printandsend.service;

import be.somedi.printandsend.entity.PatientEntity;

public interface PatientService {

    PatientEntity findFirstByExternalId(String externalId);
}
