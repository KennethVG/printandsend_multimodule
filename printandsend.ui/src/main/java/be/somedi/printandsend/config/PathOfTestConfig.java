package be.somedi.printandsend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("path-test.properties")
@Profile("test")
public class PathOfTestConfig {
}
