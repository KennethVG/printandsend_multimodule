package be.somedi.printandsend.model;

public class ExternalCaregiver {

    private String externalID;
    private String firstName;
    private String lastName;
    private String nihii;
    private String streetWithNumber;
    private String zip;
    private Boolean printProtocols;
    private UMFormat format;
    private String nihiiAddress;
    private Boolean eProtocols;
    private Boolean secondCopy;

    public ExternalCaregiver() {
    }

    public ExternalCaregiver(String externalID, String firstName, String lastName, String nihii, String streetWithNumber, String zip, Boolean printProtocols, UMFormat format, String nihiiAddress, Boolean eProtocols, Boolean secondCopy) {
        this.externalID = externalID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nihii = nihii;
        this.streetWithNumber = streetWithNumber;
        this.zip = zip;
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

    public String getNihii() {
        return nihii;
    }

    public void setNihii(String nihii) {
        this.nihii = nihii;
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
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (nihii != null ? !nihii.equals(that.nihii) : that.nihii != null) return false;
        if (streetWithNumber != null ? !streetWithNumber.equals(that.streetWithNumber) : that.streetWithNumber != null)
            return false;
        if (zip != null ? !zip.equals(that.zip) : that.zip != null) return false;
        if (printProtocols != null ? !printProtocols.equals(that.printProtocols) : that.printProtocols != null)
            return false;
        if (format != that.format) return false;
        if (nihiiAddress != null ? !nihiiAddress.equals(that.nihiiAddress) : that.nihiiAddress != null) return false;
        if (eProtocols != null ? !eProtocols.equals(that.eProtocols) : that.eProtocols != null) return false;
        return secondCopy != null ? secondCopy.equals(that.secondCopy) : that.secondCopy == null;
    }

    @Override
    public int hashCode() {
        int result = externalID != null ? externalID.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (nihii != null ? nihii.hashCode() : 0);
        result = 31 * result + (streetWithNumber != null ? streetWithNumber.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (printProtocols != null ? printProtocols.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (nihiiAddress != null ? nihiiAddress.hashCode() : 0);
        result = 31 * result + (eProtocols != null ? eProtocols.hashCode() : 0);
        result = 31 * result + (secondCopy != null ? secondCopy.hashCode() : 0);
        return result;
    }
}
