package fabiansig.historyservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    private String id;

    private String content;

    private String name;

    // TODO soll vom frontend mitgeschickt werden
    private long timestamp = System.currentTimeMillis();

}
