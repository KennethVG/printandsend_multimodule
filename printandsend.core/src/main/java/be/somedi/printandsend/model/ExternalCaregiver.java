package be.somedi.printandsend.model;

import static org.apache.commons.lang3.StringUtils.*;

public class ExternalCaregiver {

    private String externalID;
    private String firstName;
    private String lastName;
    private String nihii;
    private String phone;
    private String streetWithNumber;
    private String zip;
    private String city;
    private String title;
    private Boolean printProtocols;
    private UMFormat format;
    private String nihiiAddress;
    private Boolean eProtocols;
    private Boolean secondCopy;
    private String fullName;

    public ExternalCaregiver() {
    }

    public ExternalCaregiver(String externalID, String firstName, String lastName, String nihii, String streetWithNumber, String zip, String city, String title, Boolean printProtocols, UMFormat format, String nihiiAddress, Boolean eProtocols, Boolean secondCopy) {
        this.externalID = externalID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nihii = nihii;
        this.streetWithNumber = streetWithNumber;
        this.zip = zip;
        this.city=city;
        this.title = title;
        this.printProtocols = printProtocols;
        this.format = format;
        this.nihiiAddress = nihiiAddress;
        this.eProtocols = eProtocols;
        this.secondCopy = secondCopy;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
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

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNihii() {
        return left(nihii, 1) + "/" + substring(nihii, 1, 6) + "/" + substring(nihii, 6, 8) + "/" + right(nihii, 3);
    }

    public void setNihii(String nihii) {
        this.nihii = nihii;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreetWithNumber() {
        return streetWithNumber;
    }

    public void setStreetWithNumber(String streetWithNumber) {
        this.streetWithNumber = streetWithNumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getPrintProtocols() {
        return printProtocols;
    }

    public void setPrintProtocols(Boolean printProtocols) {
        this.printProtocols = printProtocols;
    }

    public UMFormat getFormat() {
        return format;
    }

    public void setFormat(UMFormat format) {
        this.format = format;
    }

    public String getNihiiAddress() {
        return nihiiAddress;
    }

    public void setNihiiAddress(String nihiiAddress) {
        this.nihiiAddress = nihiiAddress;
    }

    public Boolean geteProtocols() {
        return eProtocols;
    }

    public void seteProtocols(Boolean eProtocols) {
        this.eProtocols = eProtocols;
    }

    public Boolean getSecondCopy() {
        return secondCopy;
    }

    public void setSecondCopy(Boolean secondCopy) {
        this.secondCopy = secondCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalCaregiver)) return false;

        ExternalCaregiver that = (ExternalCaregiver) o;

        if (externalID != null ? !externalID.equals(that.externalID) : that.externalID != null) return false;
        return nihii != null ? nihii.equals(that.nihii) : that.nihii == null;
    }

    @Override
    public int hashCode() {
        int result = externalID != null ? externalID.hashCode() : 0;
        result = 31 * result + (nihii != null ? nihii.hashCode() : 0);
        return result;
    }
}
