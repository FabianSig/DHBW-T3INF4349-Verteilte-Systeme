package fabiansig.service;

import fabiansig.dto.OutputMessage;
import fabiansig.event.MessageReceivedEvent;
import fabiansig.model.Message;
import fabiansig.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, MessageReceivedEvent> kafkaTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${CONSUMER_GROUP_ID}")
    private String consumerGroupId;

    public OutputMessage send(Message message) {

        messageRepository.save(message);

        MessageReceivedEvent messageReceivedEvent = new MessageReceivedEvent(message, consumerGroupId);
        kafkaTemplate.send("chat", messageReceivedEvent);
        return new OutputMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }

    @KafkaListener(topics = "chat")
    public void receive(MessageReceivedEvent messageReceivedEvent) {

        if (consumerGroupId.equals(messageReceivedEvent.producerID())) {
            return;
        }
        simpMessagingTemplate.convertAndSend("/topic/messages", new OutputMessage(messageReceivedEvent.message().getName(), messageReceivedEvent.message().getContent()));
    }

}
