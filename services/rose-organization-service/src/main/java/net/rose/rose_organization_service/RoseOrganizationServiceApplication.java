package net.rose.rose_organization_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoseOrganizationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoseOrganizationServiceApplication.class, args);
	}

}
