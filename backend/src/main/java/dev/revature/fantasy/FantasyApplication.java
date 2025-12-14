package dev.revature.fantasy;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info =
                @Info(
                        title = "Fantasy API",
                        version = "1.0.0",
                        description =
                                """
                            An API to retrieve leagues and stats for fantasy football from [Sleeper](https://sleeper.com/)

                            This is not affiliated with [Sleeper](https://sleeper.com/).
                            """,
                        contact = @Contact(name = "GitHub", url = "https://github.com/251027-Java/fantasy")))
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "auth", scheme = "bearer")
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
            System.exit(1);
        }

        SpringApplication.run(FantasyApplication.class, args);
    }
}
