package com.wellmeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiOwnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiOwnerApplication.class, args);
    }
}
