package ru.tbank.contestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.tbank.contestservice.config.ApplicationConfig;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
public class ContestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContestServiceApplication.class, args);
    }

}
