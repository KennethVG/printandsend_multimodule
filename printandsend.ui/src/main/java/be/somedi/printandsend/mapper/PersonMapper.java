package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.PersonEntity;
import be.somedi.printandsend.model.medidoc.PersonMedidoc;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonMedidoc personEntityToPersonMedidoc(PersonEntity personEntity);
}
