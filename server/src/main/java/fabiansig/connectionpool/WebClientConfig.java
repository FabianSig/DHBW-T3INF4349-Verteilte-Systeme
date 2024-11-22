package fabiansig.connectionpool;

import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public HttpClient javaHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Bean
    public WebClient webClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new JdkClientHttpConnector(httpClient))
                .build();
    }
}

