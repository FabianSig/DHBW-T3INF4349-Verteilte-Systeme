package fabiansig.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private String id;

    private String content;

    private String name;

    // TODO mit @CreatedDate ersetzen. Converter in config benötigt
    private long timestamp = System.currentTimeMillis();

}