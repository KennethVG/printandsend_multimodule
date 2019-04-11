package be.somedi.printandsend.controller;

import be.somedi.printandsend.jobs.WatchServiceOfDirectory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/printjob")
@CrossOrigin("*")
public class PrintController {

    private final WatchServiceOfDirectory watchServiceOfDirectory;

    @Autowired
    public PrintController(WatchServiceOfDirectory watchServiceOfDirectory) {
        this.watchServiceOfDirectory = watchServiceOfDirectory;
    }

    @GetMapping("/start")
    public ResponseEntity<String> startPrintjob() {
        watchServiceOfDirectory.processEventsBeforeWatching();
        watchServiceOfDirectory.processEvents();
        return ResponseEntity.ok("print service is bezig ...");
    }

    @GetMapping("/stop")
    public ResponseEntity<String> stopPrintjob() {
        watchServiceOfDirectory.stopPrintJob();
        return ResponseEntity.ok("print service gestopt!");
    }
}
