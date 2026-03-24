package com.example.caloriexbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.caloriexbackend")
public class CaloriexBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(CaloriexBackendApplication.class, args);
	}
}
