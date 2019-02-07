package be.somedi.printandsend.model;

public class Patient {

    private String externalId;
    private Person person;

    public Patient() {
    }

    public Patient(String externalId, Person person) {
        this.externalId = externalId;
        this.person = person;
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
}


