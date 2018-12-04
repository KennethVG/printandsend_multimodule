package be.somedi.printandsend.repository;

import be.somedi.printandsend.entity.ExternalCaregiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalCaregiverRepository extends JpaRepository<ExternalCaregiver, Long> {

    ExternalCaregiver findFirstByExternalID(String externalId);

}
