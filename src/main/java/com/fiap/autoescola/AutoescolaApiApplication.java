package com.fiap.autoescola;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutoescolaApiApplication {
    public static void main(String[] args) {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Sao_Paulo"));
        SpringApplication.run(AutoescolaApiApplication.class, args);
    }
}
