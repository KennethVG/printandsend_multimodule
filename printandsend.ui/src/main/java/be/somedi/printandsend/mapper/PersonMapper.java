package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.PersonEntity;
import be.somedi.printandsend.model.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonEntity personToPersonEntity(Person person);

    Person personEntityToPerson(PersonEntity personEntity);

}
