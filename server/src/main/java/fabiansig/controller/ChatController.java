package fabiansig.controller;

import fabiansig.dto.OutputMessage;
import fabiansig.model.Message;
import fabiansig.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @MessageMapping("/message")
    public void send(Message message) {

        chatService.send(message);
    }

    @SubscribeMapping("/history")
    public List<OutputMessage> getHistory() {

        return chatService.getHistory();
    }

}

