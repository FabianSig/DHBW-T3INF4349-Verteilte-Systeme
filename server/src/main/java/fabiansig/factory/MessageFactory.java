package fabiansig.factory;

import fabiansig.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageFactory {

    public static final String SYSTEM_NAME = "System";

    public static Message createStrikedMessage(String username, boolean banned) {
        
        MessageType type = banned ? MessageType.BAN : MessageType.STRIKE;

        log.info("Creating message for user '{}' with type '{}'", username, type);

        return createSystemMessage(username, type);
    }

    public static Message createSystemMessage(String username, MessageType type) {

        Message systemMessage = new Message();
        systemMessage.setName(SYSTEM_NAME);
        systemMessage.setContent(username + type.getValue());

        log.debug("System message: {}", systemMessage);

        return systemMessage;
    }

    @Getter
    @AllArgsConstructor
    public enum MessageType {
        BAN(" wurde gesperrt."),
        STRIKE(" hat eine anstößige Nachricht geschickt.");

        private final String value;
    }

}
