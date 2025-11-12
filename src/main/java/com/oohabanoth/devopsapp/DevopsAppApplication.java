package com.oohabanoth.devopsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DevopsAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevopsAppApplication.class, args);
    }
}

@RestController
class HelloController {
    @GetMapping("/")
    public String hello() {
        return "Hello from devops-app! Deployed via Jenkins → Docker → Kubernetes";
    }
}
