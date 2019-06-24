package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.PatientEntity;

import java.util.List;

public interface PatientCustomRepository {

    void bestaandeDataOpnemenInLuceneIndex();

    List<PatientEntity> findAllByLastNameOrFirstName(String name);

}
