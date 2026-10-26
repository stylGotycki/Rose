package net.rose.rose_billing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RoseBillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				RoseBillingServiceApplication.class,
				args
		);
	}
}