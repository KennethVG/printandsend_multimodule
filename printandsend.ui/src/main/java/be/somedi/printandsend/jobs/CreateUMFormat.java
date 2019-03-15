package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.entity.PersonEntity;
import be.somedi.printandsend.io.ReadTxt;
import be.somedi.printandsend.io.UMWriter;
import be.somedi.printandsend.mapper.ExternalCaregiverMapper;
import be.somedi.printandsend.mapper.PatientMapper;
import be.somedi.printandsend.mapper.PersonMapper;
import be.somedi.printandsend.model.*;
import be.somedi.printandsend.service.ExternalCaregiverService;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

@Component
public class CreateUMFormat {

    private final TemplateEngine textTemplateEngine;
    private final ExternalCaregiverService externalCaregiverService;
    private final ExternalCaregiverMapper externalCaregiverMapper;
    private final LinkedExternalCaregiverService linkedExternalCaregiverService;
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final PersonMapper personMapper;
    private final UMWriter writer;

    @Value("${path-um-medidoc}")
    public Path pathMedidoc;
    @Value("${path-um-medar}")
    public Path pathMedar;
    @Value("${path-um-medicard}")
    public Path pathMedicard;

    @Autowired
    public CreateUMFormat(TemplateEngine textTemplateEngine, ExternalCaregiverService externalCaregiverService, ExternalCaregiverMapper externalCaregiverMapper, LinkedExternalCaregiverService linkedExternalCaregiverService, PatientService patientService, PatientMapper patientMapper, UMWriter writer, PersonMapper personMapper) {
        this.textTemplateEngine = textTemplateEngine;
        this.externalCaregiverService = externalCaregiverService;
        this.externalCaregiverMapper = externalCaregiverMapper;
        this.linkedExternalCaregiverService = linkedExternalCaregiverService;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.writer = writer;
        this.personMapper = personMapper;
    }

    private ExternalCaregiverEntity getExternalCaregiverFrom(ReadTxt readTxt) {
        String externalIdCaregiverFrom = readTxt.getTextAfterKey("UA");
        // Speciaal geval: Dr VAN OPSTAL (S6904 bestaat al in CC --> Dr. Luc Janssens)
        if (externalIdCaregiverFrom.equalsIgnoreCase("C6904") || externalIdCaregiverFrom.equalsIgnoreCase("D6904")) {
            externalIdCaregiverFrom = "S690V";
        } else {
            externalIdCaregiverFrom = "S".concat(externalIdCaregiverFrom.substring(1));
        }

        return externalCaregiverService.findByExternalID(externalIdCaregiverFrom);
    }

    private ExternalCaregiverEntity getExternalCaregiverTo(ReadTxt readTxt) {
        String externalIdCargiverTo = readTxt.getTextAfterKey("DR");
        return externalCaregiverService.findByExternalID(externalIdCargiverTo);
    }

    private ExternalCaregiverEntity getLinkedCaregiver(String externalId) {
        LinkedExternalCaregiverEntity linkedExternalCaregiverEntity = linkedExternalCaregiverService.findLinkedIdByExternalId(externalId);
        ExternalCaregiverEntity caregiverEntityLinked = null;
        if (linkedExternalCaregiverEntity != null) {
            caregiverEntityLinked = externalCaregiverService.findByExternalID(linkedExternalCaregiverEntity.getLinkedId());
        }
        return caregiverEntityLinked;
    }

    private Patient getPatient(boolean medidoc, ReadTxt readTxt) {
        PatientEntity patientEntity = patientService.findByExternalId(readTxt.getTextAfterKey("PC"));
        PersonEntity personEntity = patientEntity.getPerson();

        Patient patient = patientMapper.patientEntityToPatient(patientEntity);
        if (medidoc)
            patient.setPerson(personMapper.personEntityToPersonMedidoc(personEntity));

        return patient;
    }


    private Address getAddressOfPatient(ReadTxt readTxt) {
        Address address = new Address();
        String streetAndNumber = readTxt.getTextAfterKey("PS");
        Pattern pattern = Pattern.compile("([^\\d]+)\\s?(.+)");
        Matcher matcher = pattern.matcher(streetAndNumber);
        while (matcher.find()) {
            address.setStreet(matcher.group(1));
            address.setNumber(matcher.group(2));
        }
        address.setZip(readTxt.getTextAfterKey("PP"));
        address.setCity(readTxt.getTextAfterKey("PA"));
        return address;
    }

    private String getResearchDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return formatter.format(parsedDate);
    }

    private String getMedidocGender(String externalIdPatient) {
        externalIdPatient = externalIdPatient.toUpperCase();
        if (externalIdPatient.startsWith("M")) {
            return "Y";
        } else if (externalIdPatient.startsWith("V")) {
            return "X";
        } else return "Z";
    }

    private String getFormattedNihii(String nihii) {
        return left(nihii, 1) + "/" + substring(nihii, 1, 6) + "/" + substring(nihii, 6, 8) + "/" + right(nihii, 3);
    }

    private Context getDefaultContext(ReadTxt readTxt) {
        ExternalCaregiverEntity caregiverEntityTo = getExternalCaregiverTo(readTxt);
        caregiverEntityTo.setNihii(getFormattedNihii(caregiverEntityTo.getNihii()));
        ExternalCaregiverEntity caregiverEntityFrom = getExternalCaregiverFrom(readTxt);
        caregiverEntityFrom.setNihii(getFormattedNihii(caregiverEntityFrom.getNihii()));
        ExternalCaregiverEntity caregiverEntityLinked = getLinkedCaregiver(caregiverEntityTo.getExternalID());
        caregiverEntityLinked.setNihii(getFormattedNihii(caregiverEntityLinked.getNihii()));
        boolean medidoc = caregiverEntityTo.getFormat() == UMFormat.MEDIDOC;

        ExternalCaregiver caregiverTo = medidoc ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityTo) :
                externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityTo);

        ExternalCaregiver caregiverFrom = medidoc ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityFrom) :
                externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityFrom);

        ExternalCaregiver caregiverLinked = medidoc ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityLinked) :
                externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityLinked);

        Patient patient = getPatient(medidoc, readTxt);
        Person person = patient.getPerson();

        Context context = new Context();
        context.setVariable("cFrom", caregiverFrom);
        context.setVariable("cTo", caregiverLinked != null ? caregiverLinked : caregiverTo);
        context.setVariable("patient", patient);
        context.setVariable("person", person);
        context.setVariable("researchDate", getResearchDate(readTxt.getTextAfterKey("UD")));
        Address address = getAddressOfPatient(readTxt);
        if (medidoc) {
            for (int i = address.getStreet().length(); i < 24; i++) {
                address.setStreet(address.getStreet().concat(" "));
            }
        }
        context.setVariable("address", getAddressOfPatient(readTxt));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        context.setVariable("date", formatter.format(LocalDateTime.now()));
        context.setVariable("body", readTxt.getBodyOfTxt(caregiverEntityTo.getFormat()));
        return context;
    }

    public void createMedarFile(ReadTxt readTxt) {

        Context context = getDefaultContext(readTxt);

        String ref = readTxt.getTextAfterKey("PR");
        context.setVariable("ref", ref);
        context.setVariable("geslacht", getMedidocGender(getPatient(false, readTxt).getExternalId()));
        String output = textTemplateEngine.process("medar.txt", context);
        writer.write(pathMedar, output, externalCaregiverMapper.entityToExternalCaregiver(getExternalCaregiverTo(readTxt)), ref);
    }

    public void createMedicardFile(ReadTxt readTxt) {

        Context context = getDefaultContext(readTxt);
        String ref = readTxt.getTextAfterKey("PR");
        context.setVariable("ref", ref);
        context.setVariable("geslacht", left(getPatient(false, readTxt).getExternalId(), 1));

        String output = textTemplateEngine.process("medicard.txt", context);
        writer.write(pathMedicard, output, externalCaregiverMapper.entityToExternalCaregiver(getExternalCaregiverTo(readTxt)), ref);
    }

    public void createMedidocFile(ReadTxt readTxt) {
        String body = readTxt.getBodyOfTxt(UMFormat.MEDIDOC);

        Context context = getDefaultContext(readTxt);
        context.setVariable("geslacht", getMedidocGender(getPatient(false, readTxt).getExternalId()));
        String ref = substring(readTxt.getTextAfterKey("PR"), 0, 15);
        context.setVariable("ref", ref);

        int length = body.split("\n").length;
        context.setVariable("length", (length + 29));

        String output = textTemplateEngine.process("medidoc.txt", context);
        writer.write(pathMedidoc, output, externalCaregiverMapper.entityToExternalCaregiver(getExternalCaregiverTo(readTxt)), ref);
    }
}
