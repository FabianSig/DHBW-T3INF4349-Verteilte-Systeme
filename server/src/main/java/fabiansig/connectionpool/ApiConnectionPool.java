package fabiansig.connectionpool;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ApiConnectionPool {
    private final List<String> apiUris = List.of(
            "http://llm:8080"
    );
    private final AtomicInteger counter = new AtomicInteger(0);

    // Get the next API URI in round-robin fashion
    public String getNextConnection() {
        return apiUris.get(counter.getAndIncrement() % apiUris.size());
    }
}

