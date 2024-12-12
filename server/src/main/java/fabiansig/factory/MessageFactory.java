package fabiansig.factory;

import fabiansig.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageFactory {

    public static Message createStrikedMessage(String username, boolean banned) {

        log.debug("Banned has value: {}", banned);

        MessageType type = banned ? MessageType.BAN : MessageType.STRIKE;

        return createSystemMessage(username, type);
    }

    public static Message createSystemMessage(String username, MessageType type) {

        Message systemMessage = new Message();
        systemMessage.setName("System");
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
