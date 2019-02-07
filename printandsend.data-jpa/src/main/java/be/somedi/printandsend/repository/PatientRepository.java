package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    PatientEntity findByExternalId(String externalId);
}
