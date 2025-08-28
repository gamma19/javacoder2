package com.example.javacoder;

import com.example.javacoder.repository.CodeExecutionHistoryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class JavacoderApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavacoderApplication.class, args);
	}

	// Warm-up repository to ensure JPA schema generation runs on startup
	@Bean
	public Object jpaSchemaWarmup(CodeExecutionHistoryRepository repository) {
		// triggers EntityManager initialization and schema validation/update
		repository.count();
		return new Object();
	}
}
