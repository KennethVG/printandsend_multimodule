package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.PersonEntity;
import be.somedi.printandsend.repository.PersonRepository;
import be.somedi.printandsend.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PersonEntity findById(Long id) {
        return personRepository.findById(id).orElse(new PersonEntity());
    }
}
