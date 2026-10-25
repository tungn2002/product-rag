package com.tn.chatRAG.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tn.chatRAG.dto.ChatRequest;
import com.tn.chatRAG.dto.ChatResponse;
import com.tn.chatRAG.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    @Test
    @DisplayName("POST /api/chat trả lời thành công")
    void chat_Success() throws Exception {
        ChatRequest request = ChatRequest.builder()
                .message("Tìm bàn phím cơ gõ êm")
                .build();

        ChatResponse response = ChatResponse.builder()
                .answer("Shop có bàn phím Akko gõ rất êm giá 1.200.000đ")
                .build();

        when(chatService.chat(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Trả lời thành công"))
                .andExpect(jsonPath("$.data.answer").value("Shop có bàn phím Akko gõ rất êm giá 1.200.000đ"));
    }
}
