package fabiansig.connectionpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AIConnectionPool {

    private static final String COUNTER_NAME = "apiCounter";
    private final StringRedisTemplate redisTemplate;
    @Value("${ai.hostnames}")
    private String[] apiUris;

    public AIConnectionPool(StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public String getNextConnection() {

        return "http://" + apiUris[getAtomicCounter()];
    }

    //Get Atomic Counter using Redis and Round Robin Strategy
    private int getAtomicCounter() {

        Long counter = redisTemplate.opsForValue().increment(COUNTER_NAME);

        if (counter == null) {
            redisTemplate.opsForValue().set(COUNTER_NAME, "0");
            counter = 0L;
        }

        int modValue = (int) (counter % apiUris.length);

        // Reset the counter to the modulus result to prevent the value from growing indefinitely
        if (counter > Integer.MAX_VALUE) {
            redisTemplate.opsForValue().set(COUNTER_NAME, String.valueOf((long) modValue));
            log.debug("Counter reset to: {}", modValue);
        }

        return modValue;
    }

    public int getPoolSize() {

        return apiUris.length;
    }

}

