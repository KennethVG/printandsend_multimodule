package be.somedi.printandsend.model;

public class Person {

    private String firstName;
    private String lastName;
    private String inss;

    public Person() {
    }

    public Person(String firstName, String lastName, String inss) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.inss = inss;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInss() {
        return inss;
    }

    public void setInss(String inss) {
        this.inss = inss;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null) return false;
        if (lastName != null ? !lastName.equals(person.lastName) : person.lastName != null) return false;
        return inss != null ? inss.equals(person.inss) : person.inss == null;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (inss != null ? inss.hashCode() : 0);
        return result;
    }
}
