package fabiansig.llm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MockController {

    @PostMapping
    public ResponseEntity<String> mockResponse(String message) {

        return new ResponseEntity<>("true", HttpStatus.ACCEPTED);
    }
}
