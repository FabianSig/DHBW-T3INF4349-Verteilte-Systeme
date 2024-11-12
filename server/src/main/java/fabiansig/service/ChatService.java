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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

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

        if (!isMessageValid(message)) {
            return null;
        }

        messageRepository.save(message);

        MessageReceivedEvent messageReceivedEvent = new MessageReceivedEvent(message, consumerGroupId);
        kafkaTemplate.send("chat", messageReceivedEvent);
        return new OutputMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }

    private boolean isMessageValid(Message message) {
        // TODO fabian hier message überprüfen
        return true;
    }

    @KafkaListener(topics = "chat")
    public void receive(MessageReceivedEvent messageReceivedEvent) {

        if (consumerGroupId.equals(messageReceivedEvent.producerID())) {
            return;
        }
        simpMessagingTemplate.convertAndSend("/topic/messages", new OutputMessage(messageReceivedEvent.message().getName(), messageReceivedEvent.message().getContent()));
    }

    public List<OutputMessage> getHistory() {

        Iterable<Message> messages = messageRepository.findAll();
        List<OutputMessage> outputMessages = new ArrayList<>();

        StreamSupport.stream(messages.spliterator(), false)
                .map(message -> new OutputMessage(message.getName(), message.getContent()))
                .forEach(outputMessages::add);

        return outputMessages;
    }

}
