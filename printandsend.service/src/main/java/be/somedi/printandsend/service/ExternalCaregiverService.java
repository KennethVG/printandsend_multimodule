package be.somedi.printandsend.service;


import be.somedi.printandsend.entity.ExternalCaregiverEntity;

import java.util.List;

public interface ExternalCaregiverService {

    ExternalCaregiverEntity findByExternalID(String externalId);

    ExternalCaregiverEntity updateExternalCaregiver(ExternalCaregiverEntity externalCaregiverEntity);

    List<ExternalCaregiverEntity> findByName(String name);

    void bestaandeDataOpnemenInLuceneIndex();
}
