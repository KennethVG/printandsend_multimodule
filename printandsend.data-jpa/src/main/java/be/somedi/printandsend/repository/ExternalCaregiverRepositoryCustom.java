package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;

import java.util.List;

public interface ExternalCaregiverRepositoryCustom {

    void bestaandeDataOpnemenInLuceneIndex();

    List<ExternalCaregiverEntity> findAllByLastNameOrFirstName(String name);

}
