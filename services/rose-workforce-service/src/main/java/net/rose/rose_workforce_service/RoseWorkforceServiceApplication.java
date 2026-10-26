package net.rose.rose_workforce_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RoseWorkforceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				RoseWorkforceServiceApplication.class,
				args
		);
	}
}