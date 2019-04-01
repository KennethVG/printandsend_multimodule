package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    PatientEntity findByExternalId(String externalId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE dbo.Global_Patient SET general_practitioner_id = ?2 where externalId = ?1", nativeQuery = true)
    Integer updatePatient(String externalId, String doctorId);
}
