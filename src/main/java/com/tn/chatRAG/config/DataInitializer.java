package com.tn.chatRAG.config;

import com.tn.chatRAG.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Chưa có dữ liệu Product trong database. Bắt đầu nạp seed data từ data.sql...");
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
                populator.execute(dataSource);
                log.info("Nạp seed data thành công! Tổng số sản phẩm hiện tại: {}", productRepository.count());
            } catch (Exception e) {
                log.error("Lỗi khi chạy file data.sql: {}", e.getMessage(), e);
            }
        } else {
            log.info("Database đã có sẵn {} sản phẩm, bỏ qua bước seed data.", productRepository.count());
        }
    }
}
