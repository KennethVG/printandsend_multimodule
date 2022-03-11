package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface LinkedExternalCaregiverRepository extends JpaRepository<LinkedExternalCaregiverEntity, Long> {

    LinkedExternalCaregiverEntity findByExternalId(String externalId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE LinkedExternalCaregiverEntity lc SET lc.linkedId=:linkedId WHERE lc.externalId=:externalId")
    void updateLinkedExternalCaregiver(@Param("externalId") String externalId,@Param("linkedId") String linkedId);

}
