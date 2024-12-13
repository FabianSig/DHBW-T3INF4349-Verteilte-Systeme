package fabiansig.connectionpool;

import fabiansig.dto.ValidationRequest;
import fabiansig.dto.ValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final LLMConnectionPool llmConnectionPool;
    @Value("${llm.confidence.threshold}")
    private float confidenceThreshold;


    //Send Request to see if message is valid
    public boolean validateMessage(String message) {

        RestClient restClient = RestClient.builder().build();
        ValidationRequest validationRequest = new ValidationRequest(message);
        ValidationResponse validationResponse;
        String validationEndpoint = "/validate";


        int attempts = 0;

        // Anfragen in while, damit wir andere connection anfragen, wenn eine andere nicht funktioniert.
        // Das machen wir solange, bis wir alle connections probiert haben, oder eine funktionierende connection uns ein Ergebnis geliefert hat
        while (attempts < llmConnectionPool.getPoolSize()) {

            //Get the next API URI from the connection pool
            String apiUri = llmConnectionPool.getNextConnection();
            log.debug("Validation Request to: {}; Attempt: {}", apiUri + validationEndpoint, attempts);

            try {
                validationResponse = restClient.post()
                        .uri(apiUri + validationEndpoint)
                        .body(validationRequest)
                        .retrieve()
                        .body(ValidationResponse.class);

                log.debug("Validation Response: {}", validationResponse);

                if (validationResponse != null &&
                        validationResponse.success().getFirst().label().equalsIgnoreCase("non_toxic") && validationResponse.success().getFirst().score() > this.confidenceThreshold) {
                    return true;
                } else {
                    log.warn("Validation failed for message: {}", message);
                    return false;
                }
            } catch (Exception e) {
                log.error("Error during validation with {}. Connection offline", apiUri);
                attempts++;
            }
        }
        log.error("All validation attempts failed. No available connections. No Validation for current Message.");
        return false;
    }

}


