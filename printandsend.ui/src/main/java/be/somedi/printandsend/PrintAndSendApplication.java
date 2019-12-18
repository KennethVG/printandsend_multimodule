package be.somedi.printandsend;

import be.somedi.printandsend.jobs.WatchServiceOfDirectory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrintAndSendApplication {

    private static WatchServiceOfDirectory watchServiceOfDirectory;

    public PrintAndSendApplication(WatchServiceOfDirectory watchServiceOfDirectory) {
        PrintAndSendApplication.watchServiceOfDirectory = watchServiceOfDirectory;
    }

    public static void main(String[] args) {
        SpringApplication.run(PrintAndSendApplication.class, args);
        watchServiceOfDirectory.processEvents();
        watchServiceOfDirectory.processEventsBeforeWatching();
    }
}