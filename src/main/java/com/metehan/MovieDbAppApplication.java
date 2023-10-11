package com.metehan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovieDbAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieDbAppApplication.class, args);
	}

}
