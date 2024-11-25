package fabiansig.connectionpool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final LLMConnectionPool LLMConnectionPool;

    //Send Request to see if message is valid
    public boolean validateMessage(String message) {

        RestClient restClient = RestClient.builder().build();

        //Get the next API URI from the connection pool
        String apiUri = LLMConnectionPool.getNextConnection();
        String validationEndpoint = "/validate";

        log.debug("Validation Request to: {}", apiUri + validationEndpoint);

        // Boolean.TRUE.equals because reponse could be null
        return Boolean.TRUE.equals(restClient.post()
                .uri(apiUri + validationEndpoint)
                .body(message)
                .retrieve()
                .body(Boolean.class));

    }

}


