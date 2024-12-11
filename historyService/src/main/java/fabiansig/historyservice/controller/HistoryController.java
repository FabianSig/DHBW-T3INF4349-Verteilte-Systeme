package fabiansig.historyservice.controller;

import fabiansig.historyservice.model.Message;
import fabiansig.historyservice.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/history")
    public @ResponseBody List<Message> getHistory(@RequestParam long timestamp) {

        return historyService.getHistory(timestamp);
    }
}
