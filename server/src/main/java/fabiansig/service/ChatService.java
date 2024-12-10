package fabiansig.service;

import fabiansig.connectionpool.LLMService;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LLMService llmService;
    private final UserRepository userRepository;

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
                boolean banned = user.increaseStrikes();
                userRepository.save(user);
                if (!banned) {
                    return;
                }
                message = Message.builder().name("System").content(message.getName() + " wurde gesperrt.").build();
            }
            log.debug("Message valid: {}", message);

            messageRepository.save(message);

            kafkaTemplate.send("chat", message);
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
    public void receive(Message message) {

        log.debug("Received message from Kafka: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/messages", message);
    }

    public List<Message> getHistory() {

        return messageRepository.findAll();
    }

}
