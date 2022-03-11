package be.somedi.printandsend.entity;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;

@Entity
@Indexed
@Table(name = "Global_Patient")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalId;

    @OneToOne
    @JoinColumn(name = "person_id")
    @IndexedEmbedded
    private PersonEntity person;

    @OneToOne
    @JoinColumn(name = "general_practitioner_id")
    @IndexedEmbedded
    private ExternalCaregiverEntity externalCaregiver;

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    public ExternalCaregiverEntity getExternalCaregiver() {
        return externalCaregiver;
    }

    public void setExternalCaregiver(ExternalCaregiverEntity externalCaregiver) {
        this.externalCaregiver = externalCaregiver;
    }
}
