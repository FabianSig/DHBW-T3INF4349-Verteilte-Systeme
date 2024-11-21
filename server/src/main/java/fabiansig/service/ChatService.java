package fabiansig.service;

import fabiansig.connectionpool.ApiService;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    private final ApiService apiService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${CONSUMER_GROUP_ID}")
    private String consumerGroupId;

    public OutputMessage send(Message message) {
        log.info("User {} sent message: {}", message.getName(), message);

        try {
            Future<Boolean> future = executorService.submit(() -> isMessageValid(message));
            boolean isValid = future.get(5, TimeUnit.SECONDS);

            if(!isValid) {
                log.warn("Message is invalid: {}", message);
                return null;
            }
            log.debug("Message valid: {}", message);

            messageRepository.save(message);

            MessageReceivedEvent messageReceivedEvent = new MessageReceivedEvent(message, consumerGroupId);
            kafkaTemplate.send("chat", messageReceivedEvent);
            return new OutputMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
        }
        catch (Exception e) {
            log.error("Error validating message: {}", e.getMessage());
            return null;
        }
    }

    private boolean isMessageValid(Message message) {
        log.debug("Validating message: {}", message);
        try {
            // Call the ApiService to validate the message via API
            return apiService.validateMessage(message.getContent()).block(); // Blocking for simplicity
        } catch (Exception e) {
            log.error("Error during API validation: {}", e.getMessage());
            return false; // Treat errors as invalid messages
        }
    }

    @KafkaListener(topics = "chat")
    public void receive(MessageReceivedEvent messageReceivedEvent) {

        log.debug("Received message from Kafka: {}", messageReceivedEvent.message());

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
