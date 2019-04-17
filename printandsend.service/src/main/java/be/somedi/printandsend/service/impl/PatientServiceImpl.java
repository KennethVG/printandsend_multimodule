package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.repository.PatientRepository;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public PatientEntity findByExternalId(String externalId) {
        return patientRepository.findByExternalId(externalId);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<PatientEntity> findByName(String name) {
        return patientRepository.findAllByPersonLastNameOrPersonFirstName(name, name);
    }

    @Override
    public Integer updatePatientEntity(String externalId, String doctorId) {
        return patientRepository.updatePatient(externalId, doctorId);
    }
}
