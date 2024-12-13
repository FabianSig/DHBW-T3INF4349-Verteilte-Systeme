package fabiansig.connectionpool;

import fabiansig.dto.ValidationRequest;
import fabiansig.dto.ValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {


    private final RestClient restClient;
    private final RetryTemplate retryTemplate;
    private final LLMConnectionPool llmConnectionPool;
    @Value("${llm.confidence.threshold}")
    private float confidenceThreshold;
    @Value("${llm.hostnames.endpoint}")
    private  String validationEndpoint;


    /**
     * Validiert eine Nachricht durch Senden einer Anforderungsanfrage an einen externen LLM-Dienst.
     * <p>
     * Die Methode versucht n mal (wobei n die Anzahl der vorhandenen Connections im ConnectionPool ist), die Nachricht durch den LLM-Service validieren zu lassen,
     * wobei bei jedem Versuch eine Verbindung aus dem Connectionpool verwendet wird.
     * Wenn keine Verbindung verfügbar ist oder alle Versuche fehlschlagen, wird die Validierung
     * als fehlgeschlagen gelogt und die Nachricht als valide gekennzeichnet.
     * </p>
     *
     * @param message Die zu validierende Nachricht.
     * @return {@code true}, wenn die Nachricht als "non_toxic" mit ausreichender Confidence
     *         validiert wurde; andernfalls {@code false}.
     */

    public boolean validateMessage(String message) {
        try {
            return retryTemplate.execute(context -> {
                int attempts = context.getRetryCount() + 1;
                String apiUri = llmConnectionPool.getNextConnection();

                log.info("Attempt {}: Sending request to {}", attempts, apiUri + validationEndpoint);

                ValidationRequest request = new ValidationRequest(message);
                ValidationResponse validationResponse = restClient.post()
                        .uri(apiUri + validationEndpoint)
                        .body(request)
                        .retrieve()
                        .body(ValidationResponse.class);

                if (validationResponse != null &&
                        "non_toxic".equalsIgnoreCase(validationResponse.success().getFirst().label()) &&
                        validationResponse.success().getFirst().score() > this.confidenceThreshold) {
                    log.info("Message validation succeeded.");
                    return true;
                }

                throw new RestClientException("Validation failed");
            });
        } catch (RestClientException e) {
            log.error("All retries failed. No service available.");
            return true;
        }
    }

}


