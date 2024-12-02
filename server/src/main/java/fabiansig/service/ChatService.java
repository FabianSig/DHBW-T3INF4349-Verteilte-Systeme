package fabiansig.service;

import fabiansig.connectionpool.LLMService;
import fabiansig.event.MessageReceivedEvent;
import fabiansig.model.Message;
import fabiansig.model.User;
import fabiansig.repository.MessageRepository;
import fabiansig.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, MessageReceivedEvent> kafkaTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LLMService llmService;
    private final UserRepository userRepository;

    @Value("${CONSUMER_GROUP_ID}")
    private String consumerGroupId;

    public void send(Message message) {

        log.debug("User {} sent message: {}", message.getName(), message);

        try {

            if (isUserBanned(message)) {
                log.warn("User is banned: {}", message.getName());
                return;
            }

            if (!isMessageValid(message)) {
                log.warn("Message is invalid: {}", message);
                User user = userRepository.findById(message.getName())
                        .orElse(User.builder().username(message.getName()).build());
                user.increaseStrikes();
                userRepository.save(user);
                return;
            }
            log.debug("Message valid: {}", message);

            messageRepository.save(message);

            MessageReceivedEvent messageReceivedEvent = new MessageReceivedEvent(message, consumerGroupId);
            kafkaTemplate.send("chat", messageReceivedEvent);
        } catch (Exception e) {
            log.error("Error validating message: {}", e.getMessage());
        }

    }

    private boolean isUserBanned(Message message) {

        return userRepository.findById(message.getName()).map(User::isBanned).orElse(false);
    }

    private boolean isMessageValid(Message message) {

        log.debug("Validating message: {}", message);
        return llmService.validateMessage(message.getContent());
    }

    @KafkaListener(topics = "chat")
    public void receive(MessageReceivedEvent messageReceivedEvent) {

        log.debug("Received message from Kafka: {}", messageReceivedEvent.message());
        simpMessagingTemplate.convertAndSend("/topic/messages", messageReceivedEvent.message());
    }

    public List<Message> getHistory() {

        return messageRepository.findAll();
    }

}
