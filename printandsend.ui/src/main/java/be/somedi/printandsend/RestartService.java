package be.somedi.printandsend;

import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.stereotype.Service;

@Service
public class RestartService {

    private final RestartEndpoint restartEndpoint;

    public RestartService(RestartEndpoint restartEndpoint) {
        this.restartEndpoint = restartEndpoint;
    }

    public Object restartApp() {
        return restartEndpoint.restart();
    }
}