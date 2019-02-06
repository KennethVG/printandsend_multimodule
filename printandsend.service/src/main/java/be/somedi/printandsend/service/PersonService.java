package be.somedi.printandsend.service;

import be.somedi.printandsend.entity.PersonEntity;

public interface PersonService {

    PersonEntity findById(Long id);
}
