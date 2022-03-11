package be.somedi.printandsend.model;

import java.util.Objects;

public class LinkedCaregiver {

    private String externalId;
    private String linkedId;

    public LinkedCaregiver() {
    }

    public LinkedCaregiver(String externalId, String linkedId) {
        this.externalId = externalId;
        this.linkedId = linkedId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(String linkedId) {
        this.linkedId = linkedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkedCaregiver that = (LinkedCaregiver) o;

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
