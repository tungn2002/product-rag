package com.tn.chatRAG.service;

import com.tn.chatRAG.dto.ProductRequest;
import com.tn.chatRAG.dto.ProductResponse;
import com.tn.chatRAG.entity.Product;
import com.tn.chatRAG.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVectorService productVectorService;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Bắt đầu xử lý tạo sản phẩm mới: {}", request.getName());

        Product product = Product.builder()
                .name(request.getName().trim())
                .price(request.getPrice())
                .description(request.getDescription())
                .stockQuantity(request.getStockQuantity())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Tạo sản phẩm thành công với ID: {}", savedProduct.getId());

        // Bất đồng bộ nạp vector sang ChromaDB
        productVectorService.saveProductVector(savedProduct.getId());

        return ProductResponse.fromEntity(savedProduct);
    }
}
