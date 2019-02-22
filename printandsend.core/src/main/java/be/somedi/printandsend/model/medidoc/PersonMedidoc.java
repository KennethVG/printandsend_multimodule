package be.somedi.printandsend.model.medidoc;

import be.somedi.printandsend.model.Person;

import static org.apache.commons.lang3.StringUtils.substring;

public class PersonMedidoc extends Person {

    @Override
    public String getFirstName() {
        return substring(super.getFirstName(), 0, 16);
    }

    @Override
    public String getLastName() {
        String lastName = super.getLastName();
        for (int i = lastName.length(); i < 24; i++) {
            lastName = lastName.concat(" ");
        }
        return lastName;
    }

}
