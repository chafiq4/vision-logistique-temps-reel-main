package com.norival.norival_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class NorivalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NorivalBackendApplication.class, args);
	}

}
