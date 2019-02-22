package be.somedi.printandsend.controller;

import be.somedi.printandsend.jobs.WatchServiceOfDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class IndexController {

    private static final String VIEW = "index";

    private final WatchServiceOfDirectory watchServiceOfDirectory;

    @Autowired
    public IndexController(WatchServiceOfDirectory watchServiceOfDirectory) {
        this.watchServiceOfDirectory = watchServiceOfDirectory;
    }

    private String begroeting() {
        int uur = LocalDateTime.now().getHour();
        if (uur >= 6 && uur < 12) {
            return "goedeMorgen";
        }
        if (uur >= 12 && uur < 18) {
            return "goedeMiddag";
        }
        return "goedeAvond";
    }

    @GetMapping
    public ModelAndView index() {
        try {
            watchServiceOfDirectory.processEventsBeforeWatching();
            watchServiceOfDirectory.processEvents();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
        return new ModelAndView(VIEW, "begroeting", begroeting());
    }
    

}
