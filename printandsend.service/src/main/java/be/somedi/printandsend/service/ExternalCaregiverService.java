package be.somedi.printandsend.service;


import be.somedi.printandsend.entity.ExternalCaregiver;

public interface ExternalCaregiverService {

    ExternalCaregiver findFirstByExternalID(String externalId);

    ExternalCaregiver updateExternalCaregiver(ExternalCaregiver externalCaregiver);

}
