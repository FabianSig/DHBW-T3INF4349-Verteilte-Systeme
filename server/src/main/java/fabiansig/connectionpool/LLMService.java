package fabiansig.connectionpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LLMService {

    private final WebClient webClient;
    private final LLMConnectionPool LLMConnectionPool;

    public LLMService(WebClient.Builder webClientBuilder, LLMConnectionPool LLMConnectionPool) {
        this.webClient = webClientBuilder.build();
        this.LLMConnectionPool = LLMConnectionPool;
    }

    //Send Request to see if message is valid
    public Mono<Boolean> validateMessage(String message) {

        //Get the next API URI from the connection pool
        String apiUri = LLMConnectionPool.getNextConnection();
        String validationEndpoint = "/validate";

        log.debug("Validation Request to: {}", apiUri + validationEndpoint);

        return webClient.post()
                .uri(apiUri + validationEndpoint)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class)
                .map(Boolean::parseBoolean)
                .doOnNext(response -> log.debug("Validation Response (resolved): {}", response))
                .doOnError(error -> log.error("Validation failed with error: {}", error.getMessage(), error))
                .onErrorReturn(false);
    }
}


