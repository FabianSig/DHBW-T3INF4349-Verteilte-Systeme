package fabiansig.service;

import fabiansig.connectionpool.AIService;
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
    private final AIService aiService;
    private final UserRepository userRepository;

    public void send(Message message) {

        log.debug("User {} sent message: {}", message.getName(), message);

        try {

            if (isUserBanned(message)) {
                log.warn("User is banned: {}", message.getName());
                return;
            }

            message = handleMessageValidation(message);

            messageRepository.save(message);

            kafkaTemplate.send("chat", message);
        } catch (Exception e) {
            log.error("Error validating message: {}", e.getMessage());
        }

    }

    private boolean isUserBanned(Message message) {

        return userRepository.findById(message.getName()).map(User::isBanned).orElse(false);
    }

    private Message handleMessageValidation(Message message) {

        if (isMessageValid(message)) {
            log.debug("Message valid: {}", message);
            return message;
        }
        log.warn("Message is invalid: {}", message);

        User user = userRepository.findById(message.getName()).orElse(new User(message.getName()));
        boolean banned = user.increaseStrikes();
        userRepository.save(user);

        return MessageFactory.createStrikedMessage(message.getName(), banned);
    }

    private boolean isMessageValid(Message message) {

        log.debug("Validating message: {}", message);
        return aiService.validateMessage(message.getContent());
    }

    @KafkaListener(topics = "chat")
    public void receive(Message message) {

        log.debug("Received message from Kafka: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/messages", message);
    }


}
