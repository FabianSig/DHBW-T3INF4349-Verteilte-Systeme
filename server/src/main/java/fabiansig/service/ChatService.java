package fabiansig.service;

import fabiansig.dto.OutputMessage;
import fabiansig.model.Message;
import fabiansig.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;

    public OutputMessage send(Message message) {

        messageRepository.save(message);
        Iterable<Message> messages = messageRepository.findAll();
        messages.forEach(it -> log.info("Message: {}", it.toString()));
        return new OutputMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }

}
