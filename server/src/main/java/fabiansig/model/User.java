package fabiansig.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private String username;

    private int strikes = 0;

    private boolean banned = false;

    /**
     * Increases the number of strikes of the user
     * @return true if the user is banned
     */
    public boolean increaseStrikes() {
        strikes++;
        banned = strikes > 3;
        return banned;
    }

    public User(String username) {
        this.username = username;
    }
}
