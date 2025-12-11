package dev.revature.fantasy;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FantasyApplication {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.configure().load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

        } catch (Exception e) {
            // Not able to load .env file which is necessary for application to run
            System.err.println("Error loading .env file: " + e.getMessage());
            return;
        }

        SpringApplication.run(FantasyApplication.class, args);
    }
}
