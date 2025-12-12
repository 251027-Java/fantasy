package dev.revature.fantasy.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Define the base URL for your target API (e.g., the Sleeper API)
    private static final String BASE_URL = "https://api.sleeper.app/v1";

    /**
     * Creates and configures the primary WebClient bean.
     * * @param builder A WebClient.Builder provided by Spring Boot for customization.
     * @return The configured WebClient bean.
     */
    @Bean
    public WebClient sleeperWebClient() {
        return WebClient.create()
                .mutate()
                // Set the common base URL for all requests
                .baseUrl(BASE_URL)

                // Apply common headers
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
