package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.PatientEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionException;

@Repository
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class PatientCustomRepositoryImpl implements PatientCustomRepository {

    private final EntityManager entityManager;

    public PatientCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void bestaandeDataOpnemenInLuceneIndex() {
        try {
            Search.getFullTextEntityManager(entityManager).createIndexer().optimizeAfterPurge(true).optimizeOnFinish(true).startAndWait();
        } catch (InterruptedException e) {
            throw new CompletionException("Kan bestaande data niet opnemen in Lucene index", e);
        }
    }

    @Override
    public List<PatientEntity> findAllByLastNameOrFirstName(String name) {
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder builder = manager.getSearchFactory().buildQueryBuilder().forEntity(PatientEntity.class).get();
        Query query;
        if (StringUtils.containsWhitespace(name)) {
            String[] strings = StringUtils.split(name, ",");
            if (strings.length > 1) {
                query = builder.bool()
                        .should(builder.keyword().fuzzy().withEditDistanceUpTo(1).onField("person.lastName").matching(strings[0]).createQuery())
                        .should(builder.keyword().fuzzy().withEditDistanceUpTo(1).onField("person.firstName").matching(strings[1]).createQuery())
                        .createQuery();
            } else {
                query = builder.keyword().fuzzy().withEditDistanceUpTo(1).onField("person.lastName").boostedTo(1).andField("person.firstName").boostedTo(2).matching(name).createQuery();
            }
        } else {
            query = builder.keyword().fuzzy().withEditDistanceUpTo(1).onField("person.lastName").boostedTo(1).andField("person.firstName").boostedTo(2).matching(name).createQuery();
        }

        @SuppressWarnings("unchecked")
        List<PatientEntity> caregiverEntities = manager.createFullTextQuery(query, PatientEntity.class).setMaxResults(50).getResultList();
        return caregiverEntities;
    }
}
