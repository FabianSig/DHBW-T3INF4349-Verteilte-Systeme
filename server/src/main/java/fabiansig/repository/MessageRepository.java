package fabiansig.repository;

import fabiansig.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MessageRepository extends ElasticsearchRepository<Message, String> {
    Page<Message> findByName(String name, Pageable pageable);

}
