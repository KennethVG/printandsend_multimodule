package be.somedi.printandsend.service;

import be.somedi.printandsend.entity.ExternalCaregiver;
import be.somedi.printandsend.repository.ExternalCaregiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExternalCaregiverServiceImpl implements ExternalCaregiverService {

    private final ExternalCaregiverRepository externalCaregiverRepository;

    @Autowired
    public ExternalCaregiverServiceImpl(ExternalCaregiverRepository externalCaregiverRepository) {
        this.externalCaregiverRepository = externalCaregiverRepository;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public ExternalCaregiver findFirstByExternalID(String externalId) {
        if (externalId.length() == 5) {
            return externalCaregiverRepository.findFirstByExternalID(externalId);
        }
        return null;
    }

    @Override
    public ExternalCaregiver updateExternalCaregiver(ExternalCaregiver externalCaregiver) {
        return externalCaregiverRepository.saveAndFlush(externalCaregiver);
    }
}
