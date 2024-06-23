package com.nntk.sb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class Sb27Application {

    public static void main(String[] args) {
        SpringApplication.run(Sb27Application.class, args);
    }
}
