package be.somedi.printandsend.entity;

import javax.persistence.*;

@Entity
@Table(name = "dbo.Global_Patient")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;

    @Column(name = "person_id")
    private Long personId;

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Long getPersonId() {
        return personId;
    }
}
