package be.somedi.printandsend.model;

import static org.apache.commons.lang3.StringUtils.substring;

public class ExternalCaregiverMedidoc extends ExternalCaregiver {

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
