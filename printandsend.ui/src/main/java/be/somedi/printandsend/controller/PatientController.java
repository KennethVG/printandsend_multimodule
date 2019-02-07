package be.somedi.printandsend.controller;

import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

    private final PatientService patientService;


    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patient")
    public PatientEntity patient() {
        return patientService.findByExternalId("M-1030900");
    }
}
