package fabiansig.event;

import fabiansig.model.Message;

public record MessageReceivedEvent(Message message, String producerID) {

}
