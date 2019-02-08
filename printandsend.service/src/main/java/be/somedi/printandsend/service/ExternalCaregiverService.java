package be.somedi.printandsend.service;


import be.somedi.printandsend.entity.ExternalCaregiverEntity;

public interface ExternalCaregiverService {

    ExternalCaregiverEntity findByExternalID(String externalId);

    ExternalCaregiverEntity updateExternalCaregiver(ExternalCaregiverEntity externalCaregiverEntity);

}
