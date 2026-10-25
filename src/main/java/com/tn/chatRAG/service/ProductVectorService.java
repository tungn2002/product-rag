package com.tn.chatRAG.service;

import com.tn.chatRAG.entity.Product;
import com.tn.chatRAG.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductVectorService {

    private final ProductRepository productRepository;
    private final VectorStore vectorStore;

    @Async
    public void saveProductVector(Long productId) {
        log.info("Bắt đầu xử lý async vector cho sản phẩm ID={}", productId);

        productRepository.findById(productId).ifPresent(product -> {
            Map<String, Object> metadata = buildMetadata(product);
            String text = buildVectorText(product);

            List<Document> documents = TokenTextSplitter.builder()
                    .withChunkSize(800)
                    .withMinChunkSizeChars(200)
                    .withKeepSeparator(true)
                    .build()
                    .apply(List.of(new Document(text, metadata)));

            vectorStore.add(documents);
            log.info("Nạp vector vào ChromaDB thành công cho sản phẩm ID={}", product.getId());
        });
    }

    private Map<String, Object> buildMetadata(Product product) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("product_id", String.valueOf(product.getId()));
        return metadata;
    }

    private String buildVectorText(Product product) {
        StringBuilder builder = new StringBuilder();
        append(builder, "Tên sản phẩm", product.getName());
        append(builder, "Giá", product.getPrice());
        append(builder, "Mô tả", product.getDescription());
        append(builder, "Số lượng tồn kho", product.getStockQuantity());
        return builder.toString().trim();
    }

    private void append(StringBuilder builder, String label, Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return;
        }
        builder.append(label).append(": ").append(value).append(". ");
    }
}
