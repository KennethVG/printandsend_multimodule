package be.somedi.printandsend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("path-prod.properties")
@Profile("prod")
public class PathOfProdConfig {
}
