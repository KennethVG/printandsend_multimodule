package be.somedi.printandsend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RestartService {

    private final RestartEndpoint restartEndpoint;

    @Value("${url-server}")
    private String urlServer;

    public RestartService(RestartEndpoint restartEndpoint) {
        this.restartEndpoint = restartEndpoint;
    }

    public Object restartApp() {
        return restartEndpoint.restart();
    }

    public void openHomePage() {
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("rundll32 url.dll,FileProtocolHandler " + urlServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}