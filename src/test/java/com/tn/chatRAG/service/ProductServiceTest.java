package com.tn.chatRAG.service;

import com.tn.chatRAG.dto.ProductRequest;
import com.tn.chatRAG.dto.ProductResponse;
import com.tn.chatRAG.entity.Product;
import com.tn.chatRAG.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVectorService productVectorService;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Tạo sản phẩm thành công và kích hoạt async nạp vector")
    void createProduct_Success() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Bàn phím cơ Akko")
                .price(new BigDecimal("1200000"))
                .description("Bàn phím cơ không dây switch v3")
                .stockQuantity(15)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Bàn phím cơ Akko")
                .price(new BigDecimal("1200000"))
                .description("Bàn phím cơ không dây switch v3")
                .stockQuantity(15)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse response = productService.createProduct(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getPrice()).isEqualTo(request.getPrice());
        assertThat(response.getStockQuantity()).isEqualTo(request.getStockQuantity());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productVectorService, times(1)).saveProductVector(1L);
    }
}
