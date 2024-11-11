package fabiansig.service;

import fabiansig.dto.OutputMessage;
import fabiansig.model.Message;
import fabiansig.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public OutputMessage send(Message message) {

        messageRepository.save(message);
        Iterable<Message> messages = messageRepository.findAll();
        messages.forEach(it -> log.info("Message: {}", it.toString()));
        log.info("Start - Sending message {} to Kafka topic chat", message);
        kafkaTemplate.send("chat", message);
        return new OutputMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }

    @KafkaListener(topics = "chat")
    public void receive(Message message) {
        log.info("Received message from Kafka topic chat: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/messages", new OutputMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent())));
    }

}
