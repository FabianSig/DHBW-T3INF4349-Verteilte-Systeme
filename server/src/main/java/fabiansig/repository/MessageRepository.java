package fabiansig.repository;

import fabiansig.model.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageRepository extends ElasticsearchRepository<Message, String> {

    /**
     * Find all messages by timestamp greater than the given timestamp. Explicitly not TimeStampAfter because that method includes messages with equal timestamps!
     *
     * @param timestamp the timestamp to compare to
     * @return a list of messages with timestamps greater than the given timestamp
     */
    List<Message> findAllMessagesByTimestampGreaterThan(long timestamp);

}
