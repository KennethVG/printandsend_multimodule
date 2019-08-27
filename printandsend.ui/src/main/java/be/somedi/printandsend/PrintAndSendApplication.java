package be.somedi.printandsend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PrintAndSendApplication {

    private static RestartService restartService;

    public PrintAndSendApplication(RestartService restartService) {
        PrintAndSendApplication.restartService = restartService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PrintAndSendApplication.class, args);
        restartService.openHomePage();
    }
}
