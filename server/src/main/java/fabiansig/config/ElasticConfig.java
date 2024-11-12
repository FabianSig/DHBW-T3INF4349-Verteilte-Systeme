package fabiansig.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableElasticsearchRepositories
public class ElasticConfig extends ElasticsearchConfiguration {

    @Value("${elastic.host}")
    private String elasticHost;

    @Override
    @NonNull // benötigt sonst warnung
    public ClientConfiguration clientConfiguration() {

        return ClientConfiguration.builder()
                .connectedTo(elasticHost)
                .build();
    }

}
