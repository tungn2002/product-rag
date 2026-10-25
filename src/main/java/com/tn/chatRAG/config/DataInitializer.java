package com.tn.chatRAG.config;

import com.tn.chatRAG.repository.ProductRepository;
import com.tn.chatRAG.service.ProductVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductVectorService productVectorService;
    private final VectorStore vectorStore;
    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Chưa có dữ liệu Product trong database. Bắt đầu nạp seed data từ data.sql...");
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
                populator.execute(dataSource);
                log.info("Nạp seed data vào MySQL thành công! Tổng số sản phẩm hiện tại: {}", productRepository.count());
            } catch (Exception e) {
                log.error("Lỗi khi chạy file data.sql: {}", e.getMessage(), e);
            }
        } else {
            log.info("Database đã có sẵn {} sản phẩm.", productRepository.count());
        }

        // Kiểm tra xem ChromaDB đã có dữ liệu hay chưa trước khi nạp
        boolean isVectorDbEmpty = true;
        try {
            List<Document> existingDocs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query("sản phẩm")
                            .topK(1)
                            .build()
            );
            isVectorDbEmpty = (existingDocs == null || existingDocs.isEmpty());
        } catch (Exception e) {
            log.warn("ChromaDB chưa có dữ liệu hoặc collection mới khởi tạo: {}", e.getMessage());
        }

        if (isVectorDbEmpty) {
            log.info("ChromaDB đang trống. Bắt đầu nạp vector cho các sản phẩm mẫu...");
            productRepository.findAll().forEach(product -> {
                try {
                    productVectorService.saveProductVector(product.getId());
                } catch (Exception e) {
                    log.warn("Không thể nạp vector cho product id={}: {}", product.getId(), e.getMessage());
                }
            });
            log.info("Đã gửi yêu cầu nạp vector vào ChromaDB thành công!");
        } else {
            log.info("ChromaDB đã có sẵn dữ liệu vector, bỏ qua bước nạp để tránh tốn token Gemini.");
        }
    }
}
