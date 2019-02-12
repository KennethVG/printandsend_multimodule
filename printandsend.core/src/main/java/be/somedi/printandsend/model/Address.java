package be.somedi.printandsend.model;

import static org.apache.commons.lang3.StringUtils.substring;

public class Address {

    private String street;
    private String number;
    private String zip;
    private String city;

    public String getStreet() {
        for (int i = street.length(); i < 24; i++) {
            street = street.concat(" ");
        }
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return substring(number, 0, 7);
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZip() {
        return substring(zip, 0, 7);
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return substring(city, 0, 24);
    }

    public void setCity(String city) {
        this.city = city;
    }
}
