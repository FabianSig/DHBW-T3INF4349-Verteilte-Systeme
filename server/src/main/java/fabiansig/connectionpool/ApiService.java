package fabiansig.connectionpool;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

        // Make the API call to validate the message
        return webClient.post()
                .uri(apiUri + validationEndpoint)
                .bodyValue(message) // Send the message as the request body
                .retrieve()
                .bodyToMono(Boolean.class) // Expect a boolean response
                .onErrorReturn(false); // If there's an error, treat it as invalid
    }
}


