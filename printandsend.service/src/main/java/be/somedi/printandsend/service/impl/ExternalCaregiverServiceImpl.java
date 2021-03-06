package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.repository.ExternalCaregiverRepository;
import be.somedi.printandsend.service.ExternalCaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalCaregiverServiceImpl implements ExternalCaregiverService {

    private final ExternalCaregiverRepository externalCaregiverRepository;

    @Autowired
    public ExternalCaregiverServiceImpl(ExternalCaregiverRepository externalCaregiverRepository) {
        this.externalCaregiverRepository = externalCaregiverRepository;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public ExternalCaregiverEntity findByExternalID(String externalId) {
        if (externalId != null && externalId.length() == 5) {
            return externalCaregiverRepository.findFirstByExternalID(externalId);
        }
        return null;
    }

    @Override
    public ExternalCaregiverEntity updateExternalCaregiver(ExternalCaregiverEntity externalCaregiverEntity) {
        return externalCaregiverRepository.save(externalCaregiverEntity);
    }

    @Override
    public List<ExternalCaregiverEntity> findByName(String name) {
        return externalCaregiverRepository.findAllByLastNameOrFirstName(name);
    }

    @Override
    public void bestaandeDataOpnemenInLuceneIndex() {
        externalCaregiverRepository.bestaandeDataOpnemenInLuceneIndex();
    }
}
