package be.somedi.printandsend.mapper;

import be.somedi.printandsend.CareGiverDTO;
import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.model.medidoc.ExternalCaregiverMedidoc;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExternalCaregiverMapper {

    ExternalCaregiverMapper INSTANCE = Mappers.getMapper(ExternalCaregiverMapper.class);

    ExternalCaregiverEntity externalCaregiverToEntity(ExternalCaregiver externalCaregiver);

    ExternalCaregiver entityToExternalCaregiver(ExternalCaregiverEntity externalCaregiverEntity);

    ExternalCaregiverMedidoc entityToExternalCaregiverMedidoc(ExternalCaregiverEntity externalCaregiverEntity);

    ExternalCaregiverEntity dtoToEntity (CareGiverDTO careGiverDTO);
}
