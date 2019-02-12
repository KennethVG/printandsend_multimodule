package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.io.ReadTxt;
import be.somedi.printandsend.mapper.ExternalCaregiverMapper;
import be.somedi.printandsend.mapper.PatientMapper;
import be.somedi.printandsend.model.Address;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.model.Patient;
import be.somedi.printandsend.model.Person;
import be.somedi.printandsend.service.ExternalCaregiverService;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.substring;

@Component
public class CreateUMFormat {

    private final TemplateEngine textTemplateEngine;
    private final ExternalCaregiverService externalCaregiverService;
    private final ExternalCaregiverMapper externalCaregiverMapper;
    private final LinkedExternalCaregiverService linkedExternalCaregiverService;
    private final PatientService patientService;
    private final PatientMapper patientMapper;


    @Autowired
    public CreateUMFormat(TemplateEngine textTemplateEngine, ExternalCaregiverService externalCaregiverService, ExternalCaregiverMapper externalCaregiverMapper, LinkedExternalCaregiverService linkedExternalCaregiverService, PatientService patientService, PatientMapper patientMapper) {
        this.textTemplateEngine = textTemplateEngine;
        this.externalCaregiverService = externalCaregiverService;
        this.externalCaregiverMapper = externalCaregiverMapper;
        this.linkedExternalCaregiverService = linkedExternalCaregiverService;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    public void createMedidocFile(ReadTxt readTxt) {

        String externalIdCaregiverFrom = readTxt.getTextAfterKey("UA");
        // Speciaal geval: Dr VAN OPSTAL (S6904 bestaat al in CC --> Dr. Luc Janssens)
        if (externalIdCaregiverFrom.equalsIgnoreCase("C6904") || externalIdCaregiverFrom.equalsIgnoreCase("D6904")) {
            externalIdCaregiverFrom = "S690V";
        } else {
            externalIdCaregiverFrom = "S".concat(externalIdCaregiverFrom.substring(1));
        }

        String externalIdCargiverTo = readTxt.getTextAfterKey("DR");

        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(externalIdCaregiverFrom);
        ExternalCaregiver caregiverFrom = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);

        LinkedExternalCaregiverEntity linkedExternalCaregiverEntity = linkedExternalCaregiverService.findLinkedIdByExternalId(externalIdCaregiverFrom);
        ExternalCaregiver caregiverLinked = null;
        if (linkedExternalCaregiverEntity != null) {
            ExternalCaregiverEntity caregiverEntityLinked = externalCaregiverService.findByExternalID(linkedExternalCaregiverEntity.getLinkedId());
            caregiverLinked = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityLinked);
        }

        ExternalCaregiverEntity caregiverEntity1 = externalCaregiverService.findByExternalID(externalIdCargiverTo);
        ExternalCaregiver caregiverTo = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity1);

        PatientEntity patientEntity = patientService.findByExternalId(readTxt.getTextAfterKey("PC"));
        Patient patient = patientMapper.patientEntityToPatient(patientEntity);
        Person person = patient.getPerson();

        Context context = new Context();
        context.setVariable("cFrom", caregiverFrom);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        context.setVariable("date", formatter.format(LocalDateTime.now()));
        context.setVariable("cTo", caregiverLinked != null ? caregiverLinked : caregiverTo);
        context.setVariable("patient", patient);
        context.setVariable("person", person);
        context.setVariable("geslacht", getMedidocGender(patient.getExternalId()));
        context.setVariable("researchDate", getResearchDate(readTxt.getTextAfterKey("UD")));
        context.setVariable("ref", substring(readTxt.getTextAfterKey("PR"), 0, 15));
        context.setVariable("address", getAddressOfPatient(readTxt));
        context.setVariable("body", readTxt.getBodyOfTxt());
        String output = textTemplateEngine.process("medidoc.txt", context);
        System.out.println(output);
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
}
