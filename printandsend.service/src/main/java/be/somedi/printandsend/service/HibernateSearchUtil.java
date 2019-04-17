package be.somedi.printandsend.service;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import java.util.List;

public class HibernateSearchUtil {

    public static List searchByName(EntityManager entityManager, String name, Class entity, String sortField, String... fields) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer(entity).optimizeAfterPurge(true).optimizeOnFinish(true).startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(entity).get();
        Query query = queryBuilder.keyword().onFields(fields).matching(name).createQuery();

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, entity);
        fullTextQuery.setSort(new Sort(new SortField(sortField, SortField.Type.STRING)));
        fullTextQuery.setMaxResults(20);
        return fullTextQuery.getResultList();
    }
}
