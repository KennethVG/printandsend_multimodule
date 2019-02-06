package be.somedi.printandsend.service;


import be.somedi.printandsend.entity.ExternalCaregiverEntity;

public interface ExternalCaregiverService {

    ExternalCaregiverEntity findFirstByExternalID(String externalId);

    ExternalCaregiverEntity updateExternalCaregiver(ExternalCaregiverEntity externalCaregiverEntity);

}
