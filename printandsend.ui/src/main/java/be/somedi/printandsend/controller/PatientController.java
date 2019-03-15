package be.somedi.printandsend.controller;

import be.somedi.printandsend.mapper.PatientMapper;
import be.somedi.printandsend.model.Patient;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<Patient> patient(@PathVariable String id) {
        Patient patient = patientMapper.patientEntityToPatient(patientService.findByExternalId(id));
        return ResponseEntity.ok(patient);
    }
}
