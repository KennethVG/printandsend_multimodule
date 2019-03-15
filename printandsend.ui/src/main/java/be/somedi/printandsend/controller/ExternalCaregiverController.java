package be.somedi.printandsend.controller;

import be.somedi.printandsend.CareGiverDTO;
import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.jobs.CreateUMFormat;
import be.somedi.printandsend.mapper.ExternalCaregiverMapper;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.service.ExternalCaregiverService;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;

@RestController
@RequestMapping("caregiver")
public class ExternalCaregiverController {

    private static final String EXTERNALCAREGIVER_VIEW = "externalcaregiver/externalId";

    private final ExternalCaregiverService externalCaregiverService;
    private final LinkedExternalCaregiverService linkedExternalCaregiverService;
    private final ExternalCaregiverMapper externalCaregiverMapper;
    private final TemplateEngine textTemplateEngine;
    private final CreateUMFormat createUMFormat;

    @Autowired
    public ExternalCaregiverController(ExternalCaregiverService externalCaregiverService, ExternalCaregiverMapper externalCaregiverMapper, LinkedExternalCaregiverService linkedExternalCaregiverService, TemplateEngine textTemplateEngine, CreateUMFormat createUMFormat) {
        this.externalCaregiverService = externalCaregiverService;
        this.externalCaregiverMapper = externalCaregiverMapper;
        this.linkedExternalCaregiverService = linkedExternalCaregiverService;
        this.textTemplateEngine = textTemplateEngine;
        this.createUMFormat = createUMFormat;
    }

    @GetMapping("{externalId}")
    public ResponseEntity<ExternalCaregiver> getExternalCaregiver(@PathVariable String externalId) {
        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(externalId);
        ExternalCaregiver caregiverFrom = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);
        return ResponseEntity.ok(caregiverFrom);
    }

    @PostMapping("{externalId}")
    public ResponseEntity<String> updateExternalCaregiver(@PathVariable String externalId, @RequestBody ExternalCaregiver caregiver) {

        ExternalCaregiverEntity entity = externalCaregiverService.findByExternalID(externalId);
        ExternalCaregiverEntity entityToUpdate = externalCaregiverMapper.externalCaregiverToEntity(caregiver);
        entityToUpdate.setId(entity.getId());

        ExternalCaregiverEntity externalCaregiverEntity = externalCaregiverService.updateExternalCaregiver(entityToUpdate);
        return ResponseEntity.ok(externalCaregiverEntity != null ? "Dr. " + externalCaregiverEntity.getLastName() + " is succesvol geüpdatet" : "De update is niet gelukt!");
    }


//    @GetMapping("{externalId}")
//    public ModelAndView showExternalCaregiver(@PathVariable String externalId) {
//        ModelAndView modelAndView = new ModelAndView(EXTERNALCAREGIVER_VIEW);
//
//        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(externalId);
//        ExternalCaregiver caregiverFrom = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);
//        modelAndView.addObject("externalCaregiver", caregiverFrom);
//
////        LinkedExternalCaregiverEntity linkedExternalCaregiverEntity = linkedExternalCaregiverService.findLinkedIdByExternalId(externalId);
////        ExternalCaregiverEntity caregiverToEntity = externalCaregiverService.findByExternalID(linkedExternalCaregiverEntity.getLinkedId());
////        ExternalCaregiver caregiverTo = externalCaregiverMapper.entityToExternalCaregiver(caregiverToEntity);
//
//        return modelAndView;
//    }
}