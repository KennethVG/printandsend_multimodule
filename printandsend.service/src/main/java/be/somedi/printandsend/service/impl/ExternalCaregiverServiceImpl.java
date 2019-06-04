package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.repository.ExternalCaregiverRepository;
import be.somedi.printandsend.service.ExternalCaregiverService;
import be.somedi.printandsend.service.HibernateSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Service
public class ExternalCaregiverServiceImpl implements ExternalCaregiverService {

    private final ExternalCaregiverRepository externalCaregiverRepository;
    private final EntityManager entityManager;

    @Autowired
    public ExternalCaregiverServiceImpl(ExternalCaregiverRepository externalCaregiverRepository, EntityManager entityManager) {
        this.externalCaregiverRepository = externalCaregiverRepository;
        this.entityManager = entityManager;
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
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<ExternalCaregiverEntity> findByName(String name) {
        List resultList = HibernateSearchUtil.searchByName(entityManager, name, ExternalCaregiverEntity.class, "lastName", "firstName");
        if (!resultList.isEmpty() && resultList.get(0) instanceof ExternalCaregiverEntity)
            return resultList;
        else {
            return Collections.emptyList();
        }
    }
}
