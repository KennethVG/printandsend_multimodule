package be.somedi.printandsend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PrintAndSendApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(PrintAndSendApplication.class, args);
        openHomePage();
    }

    public static void openHomePage() throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("rundll32 url.dll,FileProtocolHandler " + "http://localhost:8090");
    }}
