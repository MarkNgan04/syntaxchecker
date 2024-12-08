package com.syntaxchecker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SyntaxCheckerApplication {

	public static void main(String[] args) {
		// Start the Spring Boot application
		SpringApplication.run(SyntaxCheckerApplication.class, args);

		// Define a test code to pass to the reflection utility
		String testCode = "if (x > 5) { if (x > 5) { System.out.println(x); } } else { System.out.println(x); }";

		// Use ReflectionUtils to invoke the 'isValidIfElse' method
		boolean result = ReflectionUtils.invokeIsValidIfElse(testCode);

		// Output the result
		System.out.println("Syntax is valid: " + result);
	}
}