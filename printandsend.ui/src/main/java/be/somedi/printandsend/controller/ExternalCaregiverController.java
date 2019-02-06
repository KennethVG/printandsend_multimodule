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

@Controller
@RequestMapping("")
public class ExternalCaregiverController {

    private static final String EXTERNALCAREGIVER_VIEW = "externalcaregiver/externalId";
    private final ExternalCaregiverService externalCaregiverService;

    @Autowired
    private ExternalCaregiverMapper externalCaregiverMapper;

    @Autowired
    public ExternalCaregiverController(ExternalCaregiverService externalCaregiverService) {
        this.externalCaregiverService = externalCaregiverService;
    }

    @GetMapping("{externalId}")
    public ModelAndView showExternalCaregiver(@PathVariable String externalId) {
        ModelAndView modelAndView = new ModelAndView(EXTERNALCAREGIVER_VIEW);

        ExternalCaregiverEntity caregiverEntity = externalCaregiverService.findFirstByExternalID(externalId);
        ExternalCaregiver externalCaregiver = externalCaregiverMapper.entityToExternalCaregiver(caregiverEntity);

        modelAndView.addObject("externalCaregiver", externalCaregiver);
        return modelAndView;
    }

}
