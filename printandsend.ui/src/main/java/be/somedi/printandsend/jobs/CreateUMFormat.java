package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.io.ReadTxt;
import be.somedi.printandsend.io.UMWriter;
import be.somedi.printandsend.mapper.ExternalCaregiverMapper;
import be.somedi.printandsend.mapper.PatientMapper;
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

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.substring;

@Component
public class CreateUMFormat {

    private final TemplateEngine textTemplateEngine;
    private final ExternalCaregiverService externalCaregiverService;
    private final ExternalCaregiverMapper externalCaregiverMapper;
    private final LinkedExternalCaregiverService linkedExternalCaregiverService;
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final UMWriter writer;

    @Value("${path-um-medidoc}")
    public Path pathMedidoc;
    @Value("${path-um-medar}")
    public Path pathMedar;
    @Value("${path-um-medicard}")
    public Path pathMedicard;

    @Autowired
    public CreateUMFormat(TemplateEngine textTemplateEngine, ExternalCaregiverService externalCaregiverService, ExternalCaregiverMapper externalCaregiverMapper, LinkedExternalCaregiverService linkedExternalCaregiverService, PatientService patientService, PatientMapper patientMapper, UMWriter writer) {
        this.textTemplateEngine = textTemplateEngine;
        this.externalCaregiverService = externalCaregiverService;
        this.externalCaregiverMapper = externalCaregiverMapper;
        this.linkedExternalCaregiverService = linkedExternalCaregiverService;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.writer = writer;
    }

    private ExternalCaregiver getExternalCaregiverFrom(ReadTxt readTxt) {
        String externalIdCaregiverFrom = readTxt.getTextAfterKey("UA");
        // Speciaal geval: Dr VAN OPSTAL (S6904 bestaat al in CC --> Dr. Luc Janssens)
        if (externalIdCaregiverFrom.equalsIgnoreCase("C6904") || externalIdCaregiverFrom.equalsIgnoreCase("D6904")) {
            externalIdCaregiverFrom = "S690V";
        } else {
            externalIdCaregiverFrom = "S".concat(externalIdCaregiverFrom.substring(1));
        }

        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(externalIdCaregiverFrom);
        return caregiverEntity.getFormat() == UMFormat.MEDIDOC ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntity) : externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);
    }

    private ExternalCaregiver getExternalCaregiverTo(ReadTxt readTxt) {
        String externalIdCargiverTo = readTxt.getTextAfterKey("DR");
        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(externalIdCargiverTo);
        return caregiverEntity.getFormat() == UMFormat.MEDIDOC ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntity) : externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);
    }

    private ExternalCaregiver getLinkedCaregiver(String externalId) {
        LinkedExternalCaregiverEntity linkedExternalCaregiverEntity = linkedExternalCaregiverService.findLinkedIdByExternalId(externalId);
        ExternalCaregiver caregiverLinked = null;
        if (linkedExternalCaregiverEntity != null) {
            ExternalCaregiverEntity caregiverEntityLinked = externalCaregiverService.findByExternalID(linkedExternalCaregiverEntity.getLinkedId());
            caregiverLinked = caregiverEntityLinked.getFormat() == UMFormat.MEDIDOC ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityLinked) : externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityLinked);
        }
        return caregiverLinked;
    }

    private Patient getPatient(ReadTxt readTxt) {
        PatientEntity patientEntity = patientService.findByExternalId(readTxt.getTextAfterKey("PC"));
        return patientMapper.patientEntityToPatient(patientEntity);
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

    private Context getDefaultContext(ReadTxt readTxt) {
        ExternalCaregiver caregiverFrom = getExternalCaregiverFrom(readTxt);
        ExternalCaregiver caregiverTo = getExternalCaregiverTo(readTxt);
        ExternalCaregiver caregiverLinked = getLinkedCaregiver(caregiverFrom.getExternalID());

        Patient patient = getPatient(readTxt);
        Person person = patient.getPerson();

        Context context = new Context();
        context.setVariable("cFrom", caregiverFrom);
        context.setVariable("cTo", caregiverLinked != null ? caregiverLinked : caregiverTo);
        context.setVariable("patient", patient);
        context.setVariable("person", person);
        context.setVariable("researchDate", getResearchDate(readTxt.getTextAfterKey("UD")));
        context.setVariable("address", getAddressOfPatient(readTxt));
        context.setVariable("body", readTxt.getBodyOfTxt());
        return context;
    }

    public void createMedarFile(ReadTxt readTxt) {

        Context context = getDefaultContext(readTxt);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        context.setVariable("date", formatter.format(LocalDateTime.now()));
        context.setVariable("geslacht", getMedidocGender(getPatient(readTxt).getExternalId()));
        String ref = readTxt.getTextAfterKey("PR");
        context.setVariable("ref", ref);

        String output = textTemplateEngine.process("medar.txt", context);
        writer.write(pathMedar, output, getExternalCaregiverTo(readTxt), ref);
    }

    public void createMedicardFile(ReadTxt readTxt) {

        Context context = getDefaultContext(readTxt);
        String ref = readTxt.getTextAfterKey("PR");
        context.setVariable("ref", ref);
        context.setVariable("geslacht", left(getPatient(readTxt).getExternalId(), 1));

        String output = textTemplateEngine.process("medicard.txt", context);
        writer.write(pathMedicard, output, getExternalCaregiverTo(readTxt), ref);
    }

    public void createMedidocFile(ReadTxt readTxt) {
        ExternalCaregiverMedidoc caregiverFrom = (ExternalCaregiverMedidoc) getExternalCaregiverFrom(readTxt);
        ExternalCaregiverMedidoc caregiverTo = (ExternalCaregiverMedidoc) getExternalCaregiverTo(readTxt);
        ExternalCaregiverMedidoc caregiverLinked = (ExternalCaregiverMedidoc) getLinkedCaregiver(caregiverFrom.getExternalID());

        Patient patient = getPatient(readTxt);
        Person person = patient.getPerson();

        String body = readTxt.getBodyOfTxt();

        Context context = new Context();
        context.setVariable("cFrom", caregiverFrom);
        context.setVariable("cTo", caregiverLinked != null ? caregiverLinked : caregiverTo);
        context.setVariable("patient", patient);
        context.setVariable("person", person);
        context.setVariable("researchDate", getResearchDate(readTxt.getTextAfterKey("UD")));
        context.setVariable("address", getAddressOfPatient(readTxt));
        context.setVariable("body", body);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        context.setVariable("date", formatter.format(LocalDateTime.now()));
        context.setVariable("geslacht", getMedidocGender(patient.getExternalId()));
        String ref = substring(readTxt.getTextAfterKey("PR"), 0, 15);
        context.setVariable("ref", ref);

        int length = body.split("\n").length;
        context.setVariable("length", (length + 30));

        String output = textTemplateEngine.process("medidoc.txt", context);
        writer.write(pathMedidoc, output, getExternalCaregiverTo(readTxt), ref);
    }
}
