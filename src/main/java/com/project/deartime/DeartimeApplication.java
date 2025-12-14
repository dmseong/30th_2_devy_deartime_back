package com.project.deartime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DeartimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeartimeApplication.class, args);
	}

}
