package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.repository.ExternalCaregiverRepository;
import be.somedi.printandsend.service.ExternalCaregiverService;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
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
            return externalCaregiverRepository.findByExternalID(externalId);
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
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ExternalCaregiverEntity.class).get();
        Query query = queryBuilder.keyword().onFields("lastName", "firstName").matching(name).createQuery();

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, ExternalCaregiverEntity.class);
        fullTextQuery.setMaxResults(15);
        List resultList = fullTextQuery.getResultList();
        if (!resultList.isEmpty() && resultList.get(0) instanceof ExternalCaregiverEntity)
            return resultList;
        else {
            return Collections.emptyList();
        }
    }
}
