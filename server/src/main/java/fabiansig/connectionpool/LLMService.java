package fabiansig.connectionpool;

import fabiansig.dto.ValidationRequest;
import fabiansig.dto.ValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final WebClient webClient;
    private final RetryTemplate retryTemplate;
    private final LLMConnectionPool llmConnectionPool;
    @Value("${llm.confidence.threshold}")
    private float confidenceThreshold;
    @Value("${llm.hostnames.endpoint}")
    private  String validationEndpoint;


    /**
     * Validates a message by sending a request to an external LLM service.
     * <p>
     * The method attempts n times (where n is the number of available connections in the ConnectionPool)
     * to validate the message through the LLM service, using a different connection from the pool for each attempt.
     * If no connection is available or all attempts fail, the validation is logged as failed,
     * and the message is marked as valid by default.
     * </p>
     *
     * @param message The message to be validated.
     * @return {@code true} if the message is validated as "non_toxic" with sufficient confidence;
     *         otherwise {@code false}.
     */
    public boolean validateMessage(String message) {
        try {
            return retryTemplate.execute(context -> {
                int attempts = context.getRetryCount() + 1;
                String apiUri = llmConnectionPool.getNextConnection();

                log.info("Attempt {}: Sending request to {}", attempts, apiUri + validationEndpoint);

                ValidationRequest request = new ValidationRequest(message);
                ValidationResponse validationResponse = webClient.post()
                        .uri(apiUri + validationEndpoint)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(ValidationResponse.class)
                        .toFuture().join();

                if (validationResponse != null &&
                        "non_toxic".equalsIgnoreCase(validationResponse.success().getFirst().label()) &&
                        validationResponse.success().getFirst().score() > this.confidenceThreshold) {
                    log.info("Message validation succeeded.");
                    return true;
                }
                // Throwing an exception when validation fails. This triggers the retryTemplate to retry validation using a different connection in the pool
                throw new WebClientResponseException("Validation failed", 400, "Bad Request", null, null, null);
            });
        } catch (WebClientResponseException e) {
            log.error("All retries failed. No service available.");
            return false;
        }
    }

}


