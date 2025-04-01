package com.aseubel.isee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IseeApplication {

	public static void main(String[] args) {
		SpringApplication.run(IseeApplication.class, args);
	}

}
