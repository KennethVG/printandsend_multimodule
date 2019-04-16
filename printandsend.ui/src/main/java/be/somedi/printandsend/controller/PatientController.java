package be.somedi.printandsend.controller;

import be.somedi.printandsend.dto.ListOfPatients;
import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.exceptions.PatientNotFoundException;
import be.somedi.printandsend.mapper.PatientMapper;
import be.somedi.printandsend.model.Patient;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patient")
@CrossOrigin("*")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @GetMapping("{id}")
    public ResponseEntity<Patient> patient(@PathVariable String id) {
        Patient patient = patientMapper.patientEntityToPatient(patientService.findByExternalId(id));
        if (patient == null) {
            throw new PatientNotFoundException("Patiënt met id: " + id + " niet gevonden");
        }
        return ResponseEntity.ok(patient);
    }

    @PostMapping("{id}")
    public ResponseEntity<String> updatePatient(@PathVariable String id, @RequestBody String doctorId) {
        Integer result = patientService.updatePatientEntity(id, doctorId);
        if(result == 0){
            return ResponseEntity.ok("Er is iets misgegaan tijdens het updaten. Geef een geldige Mnemonic in!");
        }
        return ResponseEntity.ok("Patiënt zijn huisarts is succesvol gewijzigd");
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<ListOfPatients> getPatienten(@PathVariable String name) {

        List<PatientEntity> patientEntityList = patientService.findByName(name);
        if(patientEntityList.isEmpty()){
            throw new PatientNotFoundException("Geen patiënten met deze naam gevonden");
        }

        ListOfPatients listOfPatients = new ListOfPatients();
        List<Patient> patients = new ArrayList<>();

        for (PatientEntity entity : patientEntityList) {
            Patient patient = patientMapper.patientEntityToPatient(entity);
            patients.add(patient);
        }
        listOfPatients.setPatients(patients);

        return ResponseEntity.ok(listOfPatients);
    }
}