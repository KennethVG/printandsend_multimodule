package be.somedi.printandsend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class IndexController {

    private static final String VIEW = "index";

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
        return new ModelAndView(VIEW, "begroeting", begroeting());
    }
    

}
