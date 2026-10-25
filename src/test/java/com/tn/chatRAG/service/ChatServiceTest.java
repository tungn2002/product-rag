package com.tn.chatRAG.service;

import com.tn.chatRAG.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient.Builder chatClientBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        chatService = new ChatService(vectorStore, chatClientBuilder);
    }

    @Test
    @DisplayName("Chat RAG thành công: Vector Search lấy Document + Metadata đưa thẳng vào Prompt")
    void chat_Success() {
        String message = "Tìm bàn phím cơ";

        // 1. Mock ChromaDB trả về Document chứa metadata product_id và text
        Document doc = Document.builder()
                .text("Tên sản phẩm: Bàn phím cơ Akko. Giá: 1200000.")
                .metadata(Map.of("product_id", "1"))
                .build();
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

        // 2. Mock ChatClient gọi LLM trả về text
        when(chatClient.prompt()
                .user(any(Consumer.class))
                .call()
                .content()).thenReturn("Shop có bàn phím Akko giá 1.200.000đ");

        // When
        ChatResponse response = chatService.chat(message);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAnswer()).isEqualTo("Shop có bàn phím Akko giá 1.200.000đ");

        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
    }
}
