package fabiansig.llm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validate")
public class MockController {

    @PostMapping
    public ResponseEntity<String> mockResponse(String message) {
        //TODO LLM LOGIC
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("true");
    }
}
