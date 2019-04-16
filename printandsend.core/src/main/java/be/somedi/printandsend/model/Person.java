package be.somedi.printandsend.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Person {

    private String firstName = "";
    private String lastName = "";
    private String inss = "";
    private LocalDate birthDate;
    private String stringBirthDate;

    public Person() {
    }

    public Person(String firstName, String lastName, String inss, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.inss = inss;
        this.birthDate = birthDate;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getStringBirthDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return dateTimeFormatter.format(birthDate);
    }

    public void setStringBirthDate(String stringBirthDate) {
        this.stringBirthDate = stringBirthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        return Objects.equals(inss, person.inss);
    }

    @Override
    public int hashCode() {
        return inss != null ? inss.hashCode() : 0;
    }
}
