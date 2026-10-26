package net.rose.rose_identity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoseIdentityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoseIdentityServiceApplication.class, args);
	}

}
