package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.PersonEntity;
import be.somedi.printandsend.model.Person;
import be.somedi.printandsend.model.medidoc.PersonMedidoc;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonEntity personToPersonEntity(Person person);

    Person personEntityToPerson(PersonEntity personEntity);

    PersonMedidoc personEntityToPersonMedidoc(PersonEntity personEntity);
}
