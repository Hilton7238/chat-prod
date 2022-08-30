package com.h.authorizecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class AuthorizeCenter {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizeCenter.class, args);
    }
}
