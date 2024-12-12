package fabiansig.factory;

import fabiansig.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MessageFactory {

    public static Message createStrikedMessage(String username, boolean banned) {

        MessageType type = banned ? MessageType.BAN : MessageType.STRIKE;
        return createSystemMessage(username, type);
    }

    public static Message createSystemMessage(String username, MessageType type) {

        return Message.builder().name("System").content(username + type.getValue()).build();
    }

    @Getter
    @AllArgsConstructor
    public enum MessageType {
        BAN(" wurde gesperrt."),
        STRIKE(" hat eine anstößige Nachricht geschickt.");

        private final String value;
    }

}
