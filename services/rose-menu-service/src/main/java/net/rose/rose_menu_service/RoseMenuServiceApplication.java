package net.rose.rose_menu_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class RoseMenuServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoseMenuServiceApplication.class, args);
	}

}
