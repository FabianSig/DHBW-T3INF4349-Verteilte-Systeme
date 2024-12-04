package fabiansig.repository;

import fabiansig.model.Message;
import jakarta.annotation.Nonnull;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageRepository extends ElasticsearchRepository<Message, String> {
    @Nonnull List<Message> findAll();
}
