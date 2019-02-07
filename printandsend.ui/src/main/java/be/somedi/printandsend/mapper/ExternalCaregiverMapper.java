package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.model.ExternalCaregiver;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExternalCaregiverMapper {

    ExternalCaregiverMapper INSTANCE = Mappers.getMapper(ExternalCaregiverMapper.class);

    ExternalCaregiverEntity externalCaregiverToEntity(ExternalCaregiver externalCaregiver);

    ExternalCaregiver entityToExternalCaregiver(ExternalCaregiverEntity externalCaregiverEntity);
}
