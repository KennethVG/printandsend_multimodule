package be.somedi.printandsend.service;

import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;

public interface LinkedExternalCaregiverService {

    LinkedExternalCaregiverEntity findLinkedIdByExternalId(String externalId);

    int updateLinkedExternalCaregiver(LinkedExternalCaregiverEntity linkedExternalCaregiverEntity);

    void deleteLinkedExternalCaregiver(LinkedExternalCaregiverEntity linkedExternalCaregiverEntity);

}
