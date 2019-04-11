package be.somedi.printandsend.jobs;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.entity.LinkedExternalCaregiverEntity;
import be.somedi.printandsend.entity.PatientEntity;
import be.somedi.printandsend.entity.PersonEntity;
import be.somedi.printandsend.io.TXTJobs;
import be.somedi.printandsend.io.UMWriter;
import be.somedi.printandsend.mapper.ExternalCaregiverMapper;
import be.somedi.printandsend.mapper.PatientMapper;
import be.somedi.printandsend.mapper.PersonMapper;
import be.somedi.printandsend.model.*;
import be.somedi.printandsend.service.ExternalCaregiverService;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import be.somedi.printandsend.service.PatientService;
import org.apache.commons.lang3.StringUtils;
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

    public ExternalCaregiverEntity getExternalCaregiverFrom(TXTJobs TXTJobs) {
        String externalIdCaregiverFrom = TXTJobs.getTextAfterKey("UA");
        // Speciaal geval: Dr VAN OPSTAL (S6904 bestaat al in CC --> Dr. Luc Janssens)
        if (externalIdCaregiverFrom.equalsIgnoreCase("C6904") || externalIdCaregiverFrom.equalsIgnoreCase("D6904")) {
            externalIdCaregiverFrom = "S690V";
        } else {
            externalIdCaregiverFrom = "S".concat(externalIdCaregiverFrom.substring(1));
        }

        return externalCaregiverService.findByExternalID(externalIdCaregiverFrom);
    }

    public ExternalCaregiverEntity getExternalCaregiverTo(TXTJobs TXTJobs) {
        String externalIdCargiverTo = TXTJobs.getTextAfterKey("DR");
        return externalCaregiverService.findByExternalID(externalIdCargiverTo);
    }

    public ExternalCaregiverEntity getLinkedCaregiver(String externalId) {
        LinkedExternalCaregiverEntity linkedExternalCaregiverEntity = linkedExternalCaregiverService.findLinkedIdByExternalId(externalId);
        ExternalCaregiverEntity caregiverEntityLinked = null;
        if (linkedExternalCaregiverEntity != null) {
            caregiverEntityLinked = externalCaregiverService.findByExternalID(linkedExternalCaregiverEntity.getLinkedId());
        }
        return caregiverEntityLinked;
    }

    private Patient getPatient(boolean medidoc, TXTJobs TXTJobs) {
        Patient patient = new Patient();
        String externalIdPatient = TXTJobs.getTextAfterKey("PC");
        if (StringUtils.startsWithIgnoreCase(externalIdPatient, "M") || startsWithIgnoreCase(externalIdPatient, "V")) {
            PatientEntity patientEntity = patientService.findByExternalId(externalIdPatient);
            PersonEntity personEntity = patientEntity.getPerson();
            patient = patientMapper.patientEntityToPatient(patientEntity);
            if (medidoc)
                patient.setPerson(personMapper.personEntityToPersonMedidoc(personEntity));
        } else {
            Person person = new Person();
            person.setFirstName(TXTJobs.getTextAfterKey("PN"));
            person.setLastName(TXTJobs.getTextAfterKey("PV"));
            String date = TXTJobs.getTextAfterKey("PD");
            person.setBirthDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("ddMMyyyy")));
            patient.setPerson(person);
        }

        return patient;
    }


    private Address getAddressOfPatient(TXTJobs TXTJobs) {
        Address address = new Address();
        String streetAndNumber = TXTJobs.getTextAfterKey("PS");
        Pattern pattern = Pattern.compile("([^\\d]+)\\s?(.+)");
        Matcher matcher = pattern.matcher(streetAndNumber);
        while (matcher.find()) {
            address.setStreet(matcher.group(1));
            address.setNumber(matcher.group(2));
        }
        address.setZip(TXTJobs.getTextAfterKey("PP"));
        address.setCity(TXTJobs.getTextAfterKey("PA"));
        return address;
    }

    private String getResearchDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return formatter.format(parsedDate);
    }

    private String getMedidocGender(String externalIdPatient) {
        if (externalIdPatient == null) return "Z";
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

    private Context getDefaultContext(TXTJobs TXTJobs) {
        ExternalCaregiverEntity caregiverEntityTo = getExternalCaregiverTo(TXTJobs);
        caregiverEntityTo.setNihii(getFormattedNihii(caregiverEntityTo.getNihii()));
        ExternalCaregiverEntity caregiverEntityFrom = getExternalCaregiverFrom(TXTJobs);
        caregiverEntityFrom.setNihii(getFormattedNihii(caregiverEntityFrom.getNihii()));
        ExternalCaregiverEntity caregiverEntityLinked = getLinkedCaregiver(caregiverEntityTo.getExternalID());
        if (caregiverEntityLinked != null)
            caregiverEntityLinked.setNihii(getFormattedNihii(caregiverEntityLinked.getNihii()));
        boolean medidoc = caregiverEntityTo.getFormat() == UMFormat.MEDIDOC;

        ExternalCaregiver caregiverTo = medidoc ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityTo) :
                externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityTo);

        ExternalCaregiver caregiverFrom = medidoc ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityFrom) :
                externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityFrom);

        ExternalCaregiver caregiverLinked = medidoc ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityLinked) :
                externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityLinked);

        Patient patient = getPatient(medidoc, TXTJobs);
        Person person = patient.getPerson();

        Context context = new Context();
        context.setVariable("cFrom", caregiverFrom);
        context.setVariable("cTo", caregiverLinked != null ? caregiverLinked : caregiverTo);
        context.setVariable("patient", patient);
        context.setVariable("person", person);
        context.setVariable("researchDate", getResearchDate(TXTJobs.getTextAfterKey("UD")));
        Address address = getAddressOfPatient(TXTJobs);
        if (medidoc) {
            for (int i = address.getStreet().length(); i < 24; i++) {
                address.setStreet(address.getStreet().concat(" "));
            }
        }
        context.setVariable("address", getAddressOfPatient(TXTJobs));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        context.setVariable("date", formatter.format(LocalDateTime.now()));
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        context.setVariable("datum", formatter.format(LocalDateTime.now()));
        context.setVariable("body", TXTJobs.getBodyOfTxt(caregiverEntityTo.getFormat()));
        return context;
    }

    public void createMedarFile(TXTJobs TXTJobs) {

        Context context = getDefaultContext(TXTJobs);

        String ref = TXTJobs.getTextAfterKey("PR");
        context.setVariable("ref", ref);
        context.setVariable("geslacht", getMedidocGender(getPatient(false, TXTJobs).getExternalId()));
        String output = textTemplateEngine.process("medar.txt", context);
        writer.write(pathMedar, output, externalCaregiverMapper.entityToExternalCaregiver(getExternalCaregiverTo(TXTJobs)), ref);
    }

    public void createMedicardFile(TXTJobs TXTJobs) {

        Context context = getDefaultContext(TXTJobs);
        String ref = TXTJobs.getTextAfterKey("PR");
        context.setVariable("ref", ref);
        context.setVariable("geslacht", left(getPatient(false, TXTJobs).getExternalId(), 1));

        String output = textTemplateEngine.process("medicard.txt", context);
        writer.write(pathMedicard, output, externalCaregiverMapper.entityToExternalCaregiver(getExternalCaregiverTo(TXTJobs)), ref);
    }

    public void createMedidocFile(TXTJobs TXTJobs) {
        String body = TXTJobs.getBodyOfTxt(UMFormat.MEDIDOC);

        Context context = getDefaultContext(TXTJobs);
        context.setVariable("geslacht", getMedidocGender(getPatient(false, TXTJobs).getExternalId()));
        String ref = substring(TXTJobs.getTextAfterKey("PR"), 0, 15);
        context.setVariable("ref", ref);

        int length = body.split("\n").length;
        context.setVariable("length", (length + 29));

        String output = textTemplateEngine.process("medidoc.txt", context);
        writer.write(pathMedidoc, output, externalCaregiverMapper.entityToExternalCaregiver(getExternalCaregiverTo(TXTJobs)), ref);
    }
}
