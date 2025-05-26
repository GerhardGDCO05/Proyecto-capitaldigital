package com.example.capitalDigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan(basePackages = "com.example.capitalDigital")
public class CapitalDigitalApplication {
	public static void main(String[] args) {
		SpringApplication.run(CapitalDigitalApplication.class, args);
	}

}
