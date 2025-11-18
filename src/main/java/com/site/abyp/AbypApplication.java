package com.site.abyp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AbypApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbypApplication.class, args);
	}

}
