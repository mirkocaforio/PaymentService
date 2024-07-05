package it.unisalento.pasproject.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RequestManagmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestManagmentServiceApplication.class, args);
    }

}
