package com.example.javacoder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JavacoderApplicationTests {

	@Test
	void contextLoads() {
		// Test that the application context loads successfully
	}

	@Test
	void mainMethodRuns() {
		// Test that the main method can be executed
		JavacoderApplication.main(new String[]{});
	}
}
