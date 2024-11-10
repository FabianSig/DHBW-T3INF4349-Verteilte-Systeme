package fabiansig.controller;

import fabiansig.dto.OutputMessage;
import fabiansig.model.Message;
import fabiansig.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {

        this.chatService = chatService;
    }

    // Handle messages sent to /app/message
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) {
        // Simulate processing or sanitization
        return chatService.send(message);
    }

}

