package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import be.somedi.printandsend.repository.LinkedExternalCaregiverRepository;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkedExternalCaregiverServiceImpl implements LinkedExternalCaregiverService {

    private final LinkedExternalCaregiverRepository linkedExternalCaregiverRepository;

    @Autowired
    public LinkedExternalCaregiverServiceImpl(LinkedExternalCaregiverRepository linkedExternalCaregiverRepository) {
        this.linkedExternalCaregiverRepository = linkedExternalCaregiverRepository;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public LinkedExternalCaregiverEntity findLinkedIdByExternalId(String externalId) {
        return linkedExternalCaregiverRepository.findByExternalId(externalId);
    }

    @Override
    @Transactional
    public int updateLinkedExternalCaregiver(LinkedExternalCaregiverEntity linkedExternalCaregiverEntity) {
        LinkedExternalCaregiverEntity searchById = findLinkedIdByExternalId(linkedExternalCaregiverEntity.getExternalId());
        if (searchById != null) {
            linkedExternalCaregiverRepository.updateLinkedExternalCaregiver(searchById.getExternalId(), linkedExternalCaregiverEntity.getLinkedId());
            return 1;
        }
        linkedExternalCaregiverRepository.save(linkedExternalCaregiverEntity);

        return 1;
    }

    @Override
    public void deleteLinkedExternalCaregiver(LinkedExternalCaregiverEntity linkedExternalCaregiverEntity) {
        linkedExternalCaregiverRepository.delete(linkedExternalCaregiverEntity);
    }
}
