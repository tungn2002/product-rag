package com.tn.chatRAG.service;

import com.tn.chatRAG.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public ChatService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public ChatResponse chat(String message) {
        log.info("Bắt đầu xử lý tin nhắn chat RAG: {}", message);

        // 1. Vector Search trên ChromaDB
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(3)
                        .build()
        );

        // 2. Lấy nội dung text kèm toàn bộ Metadata từ Document ghép vào Context
        String context = documents.stream()
                .map(doc -> String.format("%s | Metadata: %s", doc.getText(), doc.getMetadata()))
                .collect(Collectors.joining("\n---\n"));

        // 3. Khởi tạo Prompt Template
        String promptTemplate = """
                Bạn là trợ lý tư vấn bán hàng thông minh và tận tâm của cửa hàng. Vui lòng hỗ trợ khách hàng và trả lời nghiêm ngặt dựa trên tài liệu tham khảo bên dưới.

                Yêu cầu:

                1. Chỉ sử dụng thông tin trong tài liệu tham khảo, tuyệt đối không sử dụng kiến thức riêng của bạn.
                2. Nếu tài liệu tham khảo không đủ thông tin để trả lời, hãy trả lời trực tiếp: "Dựa trên thông tin hiện có, không thể xác nhận".
                3. Tuyệt đối không tự bịa đặt những thông tin không có trong tài liệu tham khảo.

                Tài liệu tham khảo:
                {context}

                Câu hỏi của người dùng:
                {question}
                """;

        // 4. Render Prompt và gọi LLM (Gemini)
        String answer = chatClient.prompt()
                .user(u -> u.text(promptTemplate)
                        .param("context", context)
                        .param("question", message))
                .call()
                .content();

        return ChatResponse.builder()
                .answer(answer)
                .build();
    }
}
