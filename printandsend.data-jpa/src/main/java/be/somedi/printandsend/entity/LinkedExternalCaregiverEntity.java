package be.somedi.printandsend.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(LinkedExternalCaregiverPk.class)
@Table(name = "dbo.Communication_LinkedExternalCaregiver")
public class LinkedExternalCaregiverEntity {

    @Id
    @Column(unique = true)
    private String externalId;
    @Id
    private String linkedId;

    public String getExternalId() {
        return externalId;
    }

    public String getLinkedId() {
        return linkedId;
    }
}

class LinkedExternalCaregiverPk implements Serializable {
    private String externalId;
    private String linkedId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedExternalCaregiverPk)) return false;

        LinkedExternalCaregiverPk that = (LinkedExternalCaregiverPk) o;

        if (!Objects.equals(externalId, that.externalId)) return false;
        return Objects.equals(linkedId, that.linkedId);
    }

    @Override
    public int hashCode() {
        int result = externalId != null ? externalId.hashCode() : 0;
        result = 31 * result + (linkedId != null ? linkedId.hashCode() : 0);
        return result;
    }
}