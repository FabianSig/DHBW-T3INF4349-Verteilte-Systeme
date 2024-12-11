package fabiansig.historyservice.service;

import fabiansig.historyservice.model.Message;
import fabiansig.historyservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final MessageRepository messageRepository;

    public List<Message> getHistory(long timestamp) {

        return messageRepository.findAllMessagesByTimestampGreaterThan(timestamp);
    }
}
