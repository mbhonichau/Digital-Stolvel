package com.digitalstokvel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DigitalStokvelApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalStokvelApplication.class, args);
    }
}
