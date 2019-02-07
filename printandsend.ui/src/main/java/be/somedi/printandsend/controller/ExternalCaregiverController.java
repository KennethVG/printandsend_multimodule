package be.somedi.printandsend.controller;

import be.somedi.printandsend.entity.ExternalCaregiverEntity;
import be.somedi.printandsend.mapper.ExternalCaregiverMapper;
import be.somedi.printandsend.model.ExternalCaregiver;
import be.somedi.printandsend.service.ExternalCaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Controller
@RequestMapping("")
public class ExternalCaregiverController {

    private static final String EXTERNALCAREGIVER_VIEW = "externalcaregiver/externalId";

    private final ExternalCaregiverService externalCaregiverService;
    private final ExternalCaregiverMapper externalCaregiverMapper;

    @Autowired
    private TemplateEngine textTemplateEngine;

    @Autowired
    public ExternalCaregiverController(ExternalCaregiverService externalCaregiverService, ExternalCaregiverMapper externalCaregiverMapper) {
        this.externalCaregiverService = externalCaregiverService;
        this.externalCaregiverMapper = externalCaregiverMapper;
    }

    @GetMapping("{externalId}")
    public ModelAndView showExternalCaregiver(@PathVariable String externalId) {
        ModelAndView modelAndView = new ModelAndView(EXTERNALCAREGIVER_VIEW);

        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findFirstByExternalID(externalId);
        ExternalCaregiver externalCaregiver = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);

        modelAndView.addObject("externalCaregiver", externalCaregiver);

        Context context = new Context();
        context.setVariable("nihii", externalCaregiver.getNihii());
        context.setVariable("naam", externalCaregiver.getLastName());
        context.setVariable("voornaam", externalCaregiver.getFirstName());
        context.setVariable("c", externalCaregiver);
        String output = textTemplateEngine.process("medidoc.txt", context);
        System.out.println(output);

        return modelAndView;
    }


}