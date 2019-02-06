package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalCaregiverRepository extends JpaRepository<ExternalCaregiverEntity, Long> {

    ExternalCaregiverEntity findFirstByExternalID(String externalId);

}
