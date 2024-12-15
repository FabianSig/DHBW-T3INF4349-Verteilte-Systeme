package fabiansig.service;

import fabiansig.connectionpool.LLMService;
import fabiansig.factory.MessageFactory;
import fabiansig.model.Message;
import fabiansig.model.User;
import fabiansig.repository.MessageRepository;
import fabiansig.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LLMService llmService;
    private final UserRepository userRepository;
    private static final String CHAT_TOPIC = "chat";

    public void send(Message message) {
        // Final variable for lambda expression
        final String userName = message.getName();

        log.debug("User {} sent message: {}", userName, message);

        User user = userRepository.findById(userName).orElseGet(() -> {
            log.info("Creating new user: {}", userName);
            return userRepository.save(new User(userName));
        });

        if (user.isBanned()) {
            log.warn("User {} is banned. Not sending message: {}", message.getName(), message);
            return;
        }

        try {
            message = handleMessageValidation(message, user);
            messageRepository.save(message);
        } catch (Exception e) {
            log.error("Error validating message: {}", e.getMessage());
        }

        try {
            kafkaTemplate.send(CHAT_TOPIC, message);
        } catch (Exception e) {
            log.error("Error sending message to Kafka: {}", e.getMessage(), e);
        }

    }


    private Message handleMessageValidation(Message message, User user) {

        if (isMessageValid(message)) {
            log.debug("Message valid: {}", message);
            return message;
        }
        // Message is not valid penalize User
        return penalizeUserForInvalidMessage(message, user);
    }

    private Message penalizeUserForInvalidMessage(Message message, User user) {
        log.warn("Message is invalid: {}", message);
        boolean banned = user.increaseStrikes();
        userRepository.save(user);

        return MessageFactory.createStrikedMessage(message.getName(), banned);
    }

    private boolean isMessageValid(Message message) {
        log.debug("Validating message: {}", message);
        return llmService.validateMessage(message.getContent());
    }

    @KafkaListener(topics = "chat")
    public void receive(Message message) {
        log.debug("Received message from Kafka: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/messages", message);
    }


}
