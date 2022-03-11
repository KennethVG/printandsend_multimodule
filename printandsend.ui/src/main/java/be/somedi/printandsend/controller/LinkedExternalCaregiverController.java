package be.somedi.printandsend.controller;

import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import be.somedi.printandsend.model.LinkedCaregiver;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("linkedCaregiver")
@CrossOrigin("*")
public class LinkedExternalCaregiverController {

    private final LinkedExternalCaregiverService linkedExternalCaregiverService;

    public LinkedExternalCaregiverController(LinkedExternalCaregiverService linkedExternalCaregiverService) {
        this.linkedExternalCaregiverService = linkedExternalCaregiverService;
    }

    @GetMapping("{externalId}")
    public ResponseEntity<LinkedExternalCaregiverEntity> getLinkedExternalCaregiver(@PathVariable String externalId) {
        return ResponseEntity.ok(linkedExternalCaregiverService.findLinkedIdByExternalId(externalId));
    }

    @PostMapping
    public ResponseEntity<String> updateExternalCaregiverWithLinkedID(@RequestBody LinkedCaregiver linkedCaregiver) {
        LinkedExternalCaregiverEntity linkedExternalCaregiverEntity = new LinkedExternalCaregiverEntity();
        linkedExternalCaregiverEntity.setExternalId(linkedCaregiver.getExternalId());
        linkedExternalCaregiverEntity.setLinkedId(linkedCaregiver.getLinkedId());

        int i = linkedExternalCaregiverService.updateLinkedExternalCaregiver(linkedExternalCaregiverEntity);
        return ResponseEntity.ok(i > 0 ? linkedCaregiver.getExternalId() + " is vanaf nu gelinkt met " + linkedCaregiver.getLinkedId() : "De update is niet gelukt!");
    }
}
