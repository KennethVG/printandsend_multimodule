package be.somedi.printandsend.controller;

import be.somedi.printandsend.jobs.WatchServiceOfDirectory;
import be.somedi.printandsend.service.ExternalCaregiverService;
import be.somedi.printandsend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController {

    private static final String VIEW = "index";
    private final WatchServiceOfDirectory watchServiceOfDirectory;

    private final ExternalCaregiverService externalCaregiverService;

    private final PatientService patientService;

    @Autowired
    public IndexController(WatchServiceOfDirectory watchServiceOfDirectory, ExternalCaregiverService externalCaregiverService, PatientService patientService) {
        this.watchServiceOfDirectory = watchServiceOfDirectory;
        this.externalCaregiverService = externalCaregiverService;
        this.patientService = patientService;
    }

    @GetMapping
    public ModelAndView index() {
        externalCaregiverService.bestaandeDataOpnemenInLuceneIndex();
        patientService.bestaandeDataOpnemenInLuceneIndex();

        watchServiceOfDirectory.processEventsBeforeWatching();
        watchServiceOfDirectory.processEvents();
        return new ModelAndView(VIEW, "printjob", "De printservice is succesvol aan het draaien!");
    }
}
