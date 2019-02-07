package be.somedi.printandsend.entity;

import javax.persistence.*;

@Entity
@Table(name = "dbo.Global_Patient")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalId;

    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonEntity person;

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
}
