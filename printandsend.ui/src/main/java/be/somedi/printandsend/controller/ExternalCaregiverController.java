package be.somedi.printandsend.controller;

import be.somedi.printandsend.dto.ListOfCaregivers;
import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.exceptions.CaregiverNotFoundException;
import be.somedi.printandsend.mapper.ExternalCaregiverMapper;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.service.ExternalCaregiverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("caregiver")
@CrossOrigin("*")
public class ExternalCaregiverController {

    private final ExternalCaregiverService externalCaregiverService;
    private final ExternalCaregiverMapper externalCaregiverMapper;

    public ExternalCaregiverController(ExternalCaregiverService externalCaregiverService, ExternalCaregiverMapper externalCaregiverMapper) {
        this.externalCaregiverService = externalCaregiverService;
        this.externalCaregiverMapper = externalCaregiverMapper;
    }

    @GetMapping("{externalId}")
    public ResponseEntity<ExternalCaregiver> getExternalCaregiver(@PathVariable String externalId) {
        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(externalId);
        if (caregiverEntity == null) {
            throw new CaregiverNotFoundException("Dokter met externalId " + externalId + " niet gevonden");
        }

        ExternalCaregiver caregiverFrom = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);
        return ResponseEntity.ok(caregiverFrom);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<ListOfCaregivers> getExternalCaregivers(@PathVariable String name) {

        List<ExternalCaregiverEntity> externalCaregiverEntityList = externalCaregiverService.findByName(name);

        ListOfCaregivers listOfCaregivers = new ListOfCaregivers();
        List<ExternalCaregiver> list = new ArrayList<>();
        for (ExternalCaregiverEntity entity : externalCaregiverEntityList) {
            ExternalCaregiver externalCaregiver = externalCaregiverMapper.entityToExternalCaregiver(entity);
            list.add(externalCaregiver);
        }
        listOfCaregivers.setCaregivers(list);


        return ResponseEntity.ok(listOfCaregivers);
    }

    @PostMapping("{externalId}")
    public ResponseEntity<String> updateExternalCaregiver(@PathVariable String externalId, @RequestBody ExternalCaregiver caregiver) {

        ExternalCaregiverEntity entity = externalCaregiverService.findByExternalID(externalId);
        ExternalCaregiverEntity entityToUpdate = externalCaregiverMapper.externalCaregiverToEntity(caregiver);
        entityToUpdate.setId(entity.getId());

        ExternalCaregiverEntity externalCaregiverEntity = externalCaregiverService.updateExternalCaregiver(entityToUpdate);
        return ResponseEntity.ok(externalCaregiverEntity != null ? "Dr. " + externalCaregiverEntity.getLastName() + " is succesvol geüpdatet" : "De update is niet gelukt!");
    }
}