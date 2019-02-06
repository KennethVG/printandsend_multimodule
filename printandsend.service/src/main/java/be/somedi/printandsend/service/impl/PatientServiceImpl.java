package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.repository.PatientRepository;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientEntity findFirstByExternalId(String externalId) {
        return patientRepository.findFirstByExternalId(externalId);
    }
}
