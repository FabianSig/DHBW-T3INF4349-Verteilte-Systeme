package fabiansig.connectionpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ApiConnectionPool {
    private final List<String> apiUris = List.of("http://llm:8080");
    private final StringRedisTemplate redisTemplate;

    public ApiConnectionPool(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getNextConnection() {

        return apiUris.get(getAtomicCounter());
    }

    private int getAtomicCounter() {
        Long counter = redisTemplate.opsForValue().increment("apiCounter");

        if (counter == null) {
            redisTemplate.opsForValue().set("apiCounter", "0");
            counter = 0L;
        }

        int modValue = (int) (counter % apiUris.size());

        // Reset the counter to the modulus result to prevent the value from growing indefinitely
        if (counter > Integer.MAX_VALUE) {
            redisTemplate.opsForValue().set("apiCounter", String.valueOf((long) modValue));
            log.info("Counter reset to: {}", modValue);
        }

        return modValue;
    }
}

