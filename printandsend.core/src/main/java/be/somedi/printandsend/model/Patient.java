package be.somedi.printandsend.model;

public class Patient {

    private String externalId;
    private Person person;
    private ExternalCaregiver externalCaregiver;

    public Patient() {
    }

    public Patient(String externalId, Person person, ExternalCaregiver externalCaregiver) {
        this.externalId = externalId;
        this.person = person;
        this.externalCaregiver = externalCaregiver;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ExternalCaregiver getExternalCaregiver() {
        return externalCaregiver;
    }

    public void setExternalCaregiver(ExternalCaregiver externalCaregiver) {
        this.externalCaregiver = externalCaregiver;
    }
}


