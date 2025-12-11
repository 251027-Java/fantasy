package dev.revature.fantasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class FantasyApplication {
    public static void main(String[] args) {
        try {
            // Load the .env file from the current working directory
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .ignoreIfMissing() // Prevents app crash if .env file is missing
                    .load();

            // Set each key-value pair as a System Property
            dotenv.entries().forEach(entry -> {
                // System Properties are generally uppercase and available to Spring
                System.setProperty(entry.getKey(), entry.getValue());
            });

        } catch (Exception e) {
            // Log if loading fails, but let Spring proceed (due to ignoreIfMissing)
            System.err.println("Error loading .env file: " + e.getMessage());
        }
        // --- END OF MANUAL LOADING ---

        // Now run the Spring Application, which will read the system properties
        SpringApplication.run(FantasyApplication.class, args);
    }
}
