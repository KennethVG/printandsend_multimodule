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
import be.somedi.printandsend.model.medidoc.PersonMedidoc;
import be.somedi.printandsend.service.ExternalCaregiverService;
import be.somedi.printandsend.service.LinkedExternalCaregiverService;
import be.somedi.printandsend.service.PatientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    static final String LEGE_BODY = "Lege body";
    static final String RIZIV_ADRES_NULL = "Riziv adres is null";
    private static final String OK = "Succesvol verzonden naar UM";

    private static final Logger LOGGER = LogManager.getLogger(CreateUMFormat.class);

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

    private Patient getPatient(boolean medidoc, TXTJobs txtJobs) {
        Patient patient = new Patient();
        String externalIdPatient = txtJobs.getTextAfterKey("PC");
        if (externalIdPatient != null && !externalIdPatient.equalsIgnoreCase("")) {
            PatientEntity patientEntity = patientService.findByExternalId(externalIdPatient);
            if (patientEntity != null) {
                PersonEntity personEntity = patientEntity.getPerson();
                patient = patientMapper.patientEntityToPatient(patientEntity);
                if (medidoc)
                    patient.setPerson(personMapper.personEntityToPersonMedidoc(personEntity));
            } else {
                patient.setPerson(createPersonFromLetter(medidoc, txtJobs));
            }
        } else {
            patient.setPerson(createPersonFromLetter(medidoc, txtJobs));
        }

        return patient;
    }

    private Person createPersonFromLetter(boolean medidoc, TXTJobs txtJobs) {
        Person person;
        if (medidoc)
            person = new PersonMedidoc();
        else person = new Person();
        person.setFirstName(txtJobs.getTextAfterKey("PV"));
        person.setLastName(txtJobs.getTextAfterKey("PN"));
        String date = txtJobs.getTextAfterKey("PD");
        person.setBirthDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("ddMMyyyy")));
        return person;
    }

    private String getFormattedNihii(String nihii) {
        return left(nihii, 1) + "/" + substring(nihii, 1, 6) + "/" + substring(nihii, 6, 8) + "/" + right(nihii, 3);
    }

    private ExternalCaregiver getExternalCaregiverTo(TXTJobs txtJobs) {
        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(txtJobs.getExternalIdOfCaregiverTo());
        if (caregiverEntity != null) {
            caregiverEntity.setNihii(getFormattedNihii(caregiverEntity.getNihii()));
            return caregiverEntity.getFormat() == UMFormat.MEDIDOC ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntity) :
                    externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);
        }
        return null;
    }

    private ExternalCaregiver getExternalCaregiverFrom(TXTJobs txtJobs) {
        String externalIdCaregiverFrom = txtJobs.getExternalIdOfCaregiverFrom();

        if (externalIdCaregiverFrom == null) {
            LOGGER.error("ExternalID van dokter die de brief geschreven heeft niet gevonden!");
            return null;
        }
        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findByExternalID(externalIdCaregiverFrom);
        if (caregiverEntity == null) return null;
        caregiverEntity.setNihii(getFormattedNihii(caregiverEntity.getNihii()));
        return caregiverEntity.getFormat() == UMFormat.MEDIDOC ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntity) :
                externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);
    }

    private ExternalCaregiver getLinkedCaregiver(String externalId) {
        LinkedExternalCaregiverEntity linkedExternalCaregiverEntity = linkedExternalCaregiverService.findLinkedIdByExternalId(externalId);
        ExternalCaregiverEntity caregiverEntityLinked = null;
        if (linkedExternalCaregiverEntity != null) {
            caregiverEntityLinked = externalCaregiverService.findByExternalID(linkedExternalCaregiverEntity.getLinkedId());
        }
        if (caregiverEntityLinked != null) {
            caregiverEntityLinked.setNihii(getFormattedNihii(caregiverEntityLinked.getNihii()));
            return caregiverEntityLinked.getFormat() == UMFormat.MEDIDOC ? externalCaregiverMapper.entityToExternalCaregiverMedidoc(caregiverEntityLinked) :
                    externalCaregiverMapper.entityToExternalCaregiver(caregiverEntityLinked);
        }
        return null;
    }


    private Context getDefaultContext(TXTJobs txtJobs, ExternalCaregiver caregiverFrom, ExternalCaregiver caregiverTo) {
        boolean medidoc = caregiverTo.getFormat() == UMFormat.MEDIDOC;

        Patient patient = getPatient(medidoc, txtJobs);
        Person person = patient.getPerson();

        Context context = new Context();
        context.setVariable("cFrom", caregiverFrom);
        context.setVariable("cTo", caregiverTo);
        context.setVariable("patient", patient);
        context.setVariable("person", person);
        context.setVariable("researchDate", txtJobs.getResearchDate());
        Address address = txtJobs.getAddressOfPatient();
        if (medidoc) {
            for (int i = address.getStreet().length(); i < 24; i++) {
                address.setStreet(address.getStreet().concat(" "));
            }
        }
        context.setVariable("address", address);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        context.setVariable("date", formatter.format(LocalDateTime.now()));
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        context.setVariable("datum", formatter.format(LocalDateTime.now()));
        context.setVariable("geboortedatum", formatter.format(person.getBirthDate()));
        context.setVariable("body", txtJobs.getBodyOfTxt(caregiverTo.getFormat()));
        return context;
    }

    private void createMedarFile(TXTJobs txtJobs, ExternalCaregiver caregiverFrom, ExternalCaregiver caregiverTo) {

        Context context = getDefaultContext(txtJobs, caregiverFrom, caregiverTo);

        String ref = txtJobs.getTextAfterKey("PR");
        context.setVariable("ref", ref);
        context.setVariable("geslacht", txtJobs.getMedidocGender());
        String output = textTemplateEngine.process("medar.txt", context);
        writer.write(pathMedar, output, caregiverTo, ref);
    }

    private void createMedicardFile(TXTJobs txtJobs, ExternalCaregiver caregiverFrom, ExternalCaregiver caregiverTo) {

        Context context = getDefaultContext(txtJobs, caregiverFrom, caregiverTo);
        String ref = txtJobs.getTextAfterKey("PR");
        context.setVariable("ref", ref);
        context.setVariable("geslacht", left(getPatient(false, txtJobs).getExternalId(), 1));

        String output = textTemplateEngine.process("medicard.txt", context);
        writer.write(pathMedicard, output, caregiverTo, ref);
    }

    private void createMedidocFile(TXTJobs txtJobs, ExternalCaregiver caregiverFrom, ExternalCaregiver caregiverTo) {
        String body = txtJobs.getBodyOfTxt(UMFormat.MEDIDOC);

        Context context = getDefaultContext(txtJobs, caregiverFrom, caregiverTo);
        context.setVariable("geslacht", txtJobs.getMedidocGender());
        String ref = substring(txtJobs.getTextAfterKey("PR"), 0, 15);
        context.setVariable("ref", ref);

        int length = body.split("\n").length;
        context.setVariable("length", (length + 29));

        String output = textTemplateEngine.process("medidoc.txt", context);
        writer.write(pathMedidoc, output, caregiverTo, ref);
    }

    String sendToUM(TXTJobs txtJobs) {
        ExternalCaregiver caregiverFrom = getExternalCaregiverFrom(txtJobs);
        ExternalCaregiver caregiverTo = getExternalCaregiverTo(txtJobs);
        ExternalCaregiver caregiverLinkedFrom;
        ExternalCaregiver caregiverLinkedTo;

        if (caregiverFrom != null) {
            if (txtJobs.getBodyOfTxt(caregiverFrom.getFormat()).equals("leeg")) {
                return LEGE_BODY;
            }
            caregiverLinkedFrom = getLinkedCaregiver(caregiverFrom.getExternalID());
            if (caregiverFrom.geteProtocols() != null && caregiverFrom.geteProtocols()) {
                LOGGER.info("Brief proberen verzenden naar arts die de brief geschreven heeft");
                sendToUm(txtJobs, caregiverFrom, caregiverFrom);
            }
            if (caregiverLinkedFrom != null && caregiverLinkedFrom.geteProtocols()) {
                LOGGER.info("Kopie van de brief proberen te verzenden naar de gelinkte arts");
                sendToUm(txtJobs, caregiverFrom, caregiverLinkedFrom);
            }
        }

        if (caregiverTo != null) {
            if (caregiverTo.geteProtocols() != null && caregiverTo.geteProtocols()) {
                if (caregiverTo.getNihiiAddress() == null || caregiverTo.getNihiiAddress().equals("NULL")) {
                    return RIZIV_ADRES_NULL;
                } else {
                    LOGGER.info("Brief proberen verzenden naar arts in AAN/CC");
                    sendToUm(txtJobs, caregiverFrom, caregiverTo);
                }
            }
            caregiverLinkedTo = getLinkedCaregiver(caregiverTo.getExternalID());
            if (caregiverLinkedTo != null && caregiverLinkedTo.geteProtocols()) {
                LOGGER.info("Kopie van de brief proberen te verzenden naar de gelinkte arts in AAN/CC");
                sendToUm(txtJobs, caregiverFrom, caregiverLinkedTo);
            }
        }
        return OK;
    }

    private void sendToUm(TXTJobs txtJobs, ExternalCaregiver caregiverFrom, ExternalCaregiver caregiverTo) {
        LOGGER.info("CaregiverFrom= " + caregiverFrom);
        LOGGER.info("CaregiverTo= " + caregiverTo);
        UMFormat format = caregiverTo.getFormat();
        if (format == null) {
            LOGGER.error(caregiverTo.getExternalID() + " heeft geen (geldig) formaat ingevuld.");
        } else {
            switch (format) {
                case MEDICARD:
                    createMedicardFile(txtJobs, caregiverFrom, caregiverTo);
                    break;
                case MEDAR:
                    createMedarFile(txtJobs, caregiverFrom, caregiverTo);
                    break;
                default:
                    createMedidocFile(txtJobs, caregiverFrom, caregiverTo);
                    break;
            }
            LOGGER.info("Er is succesvol een brief in " + format.name() + " formaat verzonden naar Dr. " + caregiverTo.getLastName());
        }

    }
}