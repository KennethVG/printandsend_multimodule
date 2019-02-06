package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import be.somedi.printandsend.repository.LinkedExternalCaregiverRepository;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkedExternalCaregiverServiceImpl implements LinkedExternalCaregiverService {

    private final LinkedExternalCaregiverRepository linkedExternalCaregiverRepository;

    @Autowired
    public LinkedExternalCaregiverServiceImpl(LinkedExternalCaregiverRepository linkedExternalCaregiverRepository) {
        this.linkedExternalCaregiverRepository = linkedExternalCaregiverRepository;
    }

    @Override
    public LinkedExternalCaregiverEntity findLinkedIdByExternalId(String externalId) {
        return linkedExternalCaregiverRepository.findByExternalId(externalId);
    }

    @Override
    public int updateLinkedExternalCaregiver(LinkedExternalCaregiverEntity linkedExternalCaregiverEntity) {

        LinkedExternalCaregiverEntity searchById = findLinkedIdByExternalId(linkedExternalCaregiverEntity.getExternalId());
        if (searchById != null) {
            linkedExternalCaregiverRepository.updateLinkedExternalCaregiver(searchById.getExternalId(), linkedExternalCaregiverEntity.getLinkedId());
            return 1;
        }
        LinkedExternalCaregiverEntity caregiverEntity = linkedExternalCaregiverRepository.save(linkedExternalCaregiverEntity);

        return caregiverEntity != null ? 1 : 0;
    }

    @Override
    public void deleteLinkedExternalCaregiver(LinkedExternalCaregiverEntity linkedExternalCaregiverEntity) {
        linkedExternalCaregiverRepository.delete(linkedExternalCaregiverEntity);
    }
}
