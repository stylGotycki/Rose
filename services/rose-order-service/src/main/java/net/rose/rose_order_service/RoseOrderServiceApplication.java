package net.rose.rose_order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RoseOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				RoseOrderServiceApplication.class,
				args
		);
	}
}