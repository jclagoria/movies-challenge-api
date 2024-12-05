package com.directa24.main.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Directa24BackEndDevChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(Directa24BackEndDevChallengeApplication.class, args);
	}

}
