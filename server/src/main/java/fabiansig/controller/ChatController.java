package fabiansig.controller;
import fabiansig.dto.Message;
import fabiansig.dto.OutputMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class ChatController {

    // Handle messages sent to /app/message
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) throws Exception {
        // Simulate processing or sanitization
        return new OutputMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }
}
