package net.rose.rose_access_service;

import net.rose.rose_access_service.core.config.restclient.IdentityAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(IdentityAuthProperties.class)
public class RoseAccessServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoseAccessServiceApplication.class, args);
	}

}
