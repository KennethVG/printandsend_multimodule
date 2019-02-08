package be.somedi.printandsend.entity;

import be.somedi.printandsend.model.UMFormat;

import javax.persistence.*;

@Entity
@Table(name = "dbo.Communication_ExternalCaregiver")
public class ExternalCaregiverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalID;
    private String firstName;
    private String lastName;
    private String nihii;
    private String streetWithNumber;
    private String zip;
    private Boolean printProtocols;
    @Enumerated(EnumType.STRING)
    private UMFormat format;
    private String nihiiAddress;
    private Boolean eProtocols;
    private Boolean secondCopy;

    public ExternalCaregiverEntity() {
    }

    public ExternalCaregiverEntity(String externalID, String firstName, String lastName, String nihii, String streetWithNumber, String zip, Boolean printProtocols, UMFormat format, String nihiiAddress, Boolean eProtocols, Boolean secondCopy) {
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

    public Long getId() {
        return id;
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
}
