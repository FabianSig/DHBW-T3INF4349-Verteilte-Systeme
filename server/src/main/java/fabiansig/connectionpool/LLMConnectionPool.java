package fabiansig.connectionpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LLMConnectionPool {

    private static final String COUNTER_NAME = "apiCounter";
    private final StringRedisTemplate redisTemplate;
    @Value("${llm.hostnames}")
    private String[] apiUris;

    public LLMConnectionPool(StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public String getNextConnection() {

        return apiUris[getNextConnectionPoolIndex()];
    }

    /**
     * Gibt den nächst verfügbare Connection-Index mithilfe von Round Robin und Redis
     * @return Nächster verfügbarer Connection-Index
     */
    private int getNextConnectionPoolIndex() {

        Long counter = redisTemplate.opsForValue().increment(COUNTER_NAME, 1);

        if (counter == null) {
            throw new IllegalStateException("Redis increment operation returned null.");
        }

        return (int) (counter % apiUris.length);
    }

    public int getPoolSize() {
        return apiUris.length;
    }

}

