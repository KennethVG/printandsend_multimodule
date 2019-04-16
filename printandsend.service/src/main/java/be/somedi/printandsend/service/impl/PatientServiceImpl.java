package be.somedi.printandsend.service.impl;

import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.repository.PatientRepository;
import be.somedi.printandsend.service.PatientService;
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
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final EntityManager entityManager;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, EntityManager entityManager) {
        this.patientRepository = patientRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public PatientEntity findByExternalId(String externalId) {
        return patientRepository.findByExternalId(externalId);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<PatientEntity> findByName(String name) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(PatientEntity.class).get();
        Query query = queryBuilder.keyword().onFields("person.lastName", "person.firstName").matching(name).createQuery();

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, PatientEntity.class);
        fullTextQuery.setMaxResults(20);
        List resultList = fullTextQuery.getResultList();

        if (!resultList.isEmpty() && resultList.get(0) instanceof PatientEntity)
            return resultList;
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public Integer updatePatientEntity(String externalId, String doctorId) {
        return patientRepository.updatePatient(externalId, doctorId);
    }
}
