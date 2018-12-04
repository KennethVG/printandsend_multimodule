package be.somedi.printandsend.entity;

import be.somedi.printandsend.model.UMFormat;

import javax.persistence.*;

@Entity
@Table(name = "dbo.Communication_ExternalCaregiver")
public class ExternalCaregiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public ExternalCaregiver() {
    }

    public Long getId() {
        return id;
    }

    public String getExternalID() {
        return externalID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNihii() {
        return nihii;
    }

    public String getStreetWithNumber() {
        return streetWithNumber;
    }

    public String getZip() {
        return zip;
    }

    public Boolean getPrintProtocols() {
        return printProtocols;
    }

    public UMFormat getFormat() {
        return format;
    }

    public String getNihiiAddress() {
        return nihiiAddress;
    }

    public Boolean geteProtocols() {
        return eProtocols;
    }

    public Boolean getSecondCopy() {
        return secondCopy;
    }
}
