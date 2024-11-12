package fabiansig.controller;

import fabiansig.dto.OutputMessage;
import fabiansig.model.Message;
import fabiansig.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // Handle messages sent to /app/message
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) {
        // Simulate processing or sanitization
        return chatService.send(message);
    }

    @SubscribeMapping("/message")
    public List<OutputMessage> getHistory(SimpMessageHeaderAccessor headerAccessor) {
        // Fetch history from chat service
        List<OutputMessage> history = chatService.getHistory();

        // Get session ID for the current client
        String sessionId = headerAccessor.getSessionId();

        // Send the history to the specific client using their session ID
        assert sessionId != null;
        log.info("Sending history to session: {}", sessionId);
        history.subList(0, 5).forEach(outputMessage -> log.info("Message: {}", outputMessage.getContent()));
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/history", history);
        return history;
    }

}

