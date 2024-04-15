package com.papershare.papershare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan("com.papershare.papershare.model")
public class PaperShareApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaperShareApplication.class, args);
	}

}
