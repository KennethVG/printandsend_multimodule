package be.somedi.printandsend.controller;

import be.somedi.printandsend.jobs.WatchServiceOfDirectory;
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

    @Autowired
    public IndexController(WatchServiceOfDirectory watchServiceOfDirectory) {
        this.watchServiceOfDirectory = watchServiceOfDirectory;
    }

    @GetMapping
    public ModelAndView index() {
        watchServiceOfDirectory.processEventsBeforeWatching();
        watchServiceOfDirectory.processEvents();
        return new ModelAndView(VIEW, "begroeting", "Hello World!");
    }
}
