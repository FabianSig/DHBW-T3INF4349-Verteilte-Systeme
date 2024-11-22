package fabiansig.connectionpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ApiService {

    private final WebClient webClient;
    private final ApiConnectionPool apiConnectionPool;

    public ApiService(WebClient.Builder webClientBuilder, ApiConnectionPool apiConnectionPool) {
        this.webClient = webClientBuilder.build();
        this.apiConnectionPool = apiConnectionPool;
    }

    // Call the validation API to check if the message is valid
    public Mono<Boolean> validateMessage(String message) {
        // Get the next API URI from the connection pool
        String apiUri = apiConnectionPool.getNextConnection();
        String validationEndpoint = "/validate"; // Example validation endpoint

        log.debug("Validation Request to: {}", apiUri + validationEndpoint);

        return webClient.post()
                .uri(apiUri + validationEndpoint)
                .bodyValue(message) // Send the message as the request body
                .retrieve()
                .bodyToMono(String.class)
                .map(Boolean::parseBoolean)// Expect a boolean response
                .doOnNext(response -> log.debug("Validation Response (resolved): {}", response)) // Log the resolved response
                .doOnError(error -> log.error("Validation failed with error: {}", error.getMessage(), error)) // Log errors
                .onErrorReturn(false); // If there's an error, treat it as invalid
    }
}


