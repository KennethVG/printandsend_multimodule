package be.somedi.printandsend.entity;

import be.somedi.printandsend.model.UMFormat;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

@Entity
@Indexed
@Table(name = "dbo.Communication_ExternalCaregiver")
public class ExternalCaregiverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String externalID;
    @Column(nullable = false)
    @Field
    private String firstName;
    @Column(nullable = false)
    @Field
    private String lastName;
    private String nihii;
    private String streetWithNumber;
    private String zip;
    private String city;
    private String title;
    private String phone;
    private Boolean active = Boolean.TRUE;
    private Boolean printProtocols;
    @Enumerated(EnumType.STRING)
    private UMFormat format;
    private String nihiiAddress;
    private Boolean eProtocols;
    private Boolean secondCopy;


    public ExternalCaregiverEntity() {
    }

    public ExternalCaregiverEntity(String externalID, String firstName, String lastName, String nihii, String streetWithNumber, String zip, String city, String title, String phone, Boolean active, Boolean printProtocols, UMFormat format, String nihiiAddress, Boolean eProtocols, Boolean secondCopy) {
        this.externalID = externalID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nihii = nihii;
        this.streetWithNumber = streetWithNumber;
        this.zip = zip;
        this.city = city;
        this.title = title;
        this.phone = phone;
        this.active = active;
        this.printProtocols = printProtocols;
        this.format = format;
        this.nihiiAddress = nihiiAddress;
        this.eProtocols = eProtocols;
        this.secondCopy = secondCopy;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
