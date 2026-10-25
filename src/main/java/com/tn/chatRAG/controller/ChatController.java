package com.tn.chatRAG.controller;

import com.tn.chatRAG.dto.ApiResponse;
import com.tn.chatRAG.dto.ChatRequest;
import com.tn.chatRAG.dto.ChatResponse;
import com.tn.chatRAG.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@RequestBody ChatRequest request) {
        log.info("Nhận request chat: {}", request.getMessage());
        ChatResponse response = chatService.chat(request.getMessage());
        return ResponseEntity.ok(ApiResponse.success(response, "Trả lời thành công"));
    }
}
