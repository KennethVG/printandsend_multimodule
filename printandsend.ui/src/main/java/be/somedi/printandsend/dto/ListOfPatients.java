package be.somedi.printandsend.dto;

import be.somedi.printandsend.model.Patient;

import java.util.List;

public class ListOfPatients {

    private List<Patient> patients;

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
}
