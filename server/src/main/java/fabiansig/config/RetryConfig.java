package fabiansig.config;

import fabiansig.connectionpool.LLMConnectionPool;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
@RequiredArgsConstructor
public class RetryConfig {

    private final LLMConnectionPool llmConnectionPool;

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(llmConnectionPool.getPoolSize())
                .fixedBackoff(2000)
                .build();
    }
}
