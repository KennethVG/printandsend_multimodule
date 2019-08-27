package be.somedi.printandsend.controller;

import be.somedi.printandsend.PrintAndSendApplication;
import be.somedi.printandsend.RestartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class RestartController {

    private final RestartService restartService;

    public RestartController(RestartService restartService) {
        this.restartService = restartService;
    }

    @GetMapping("/restart")
    public ResponseEntity<Object> restartBackend() {
        Object object = restartService.restartApp();
        if (object != null) {
            try {
                Thread.sleep(2500);
                restartService.openHomePage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok(object);
        }
        return ResponseEntity.badRequest().build();
    }
}