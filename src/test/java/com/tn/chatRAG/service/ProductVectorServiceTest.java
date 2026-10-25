package com.tn.chatRAG.service;

import com.tn.chatRAG.entity.Product;
import com.tn.chatRAG.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductVectorServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private ProductVectorService productVectorService;

    @Test
    @DisplayName("Nạp vector sản phẩm vào VectorStore thành công theo productId")
    void saveProductVector_Success() {
        Product product = Product.builder()
                .id(1L)
                .name("Bàn phím cơ Akko")
                .price(new BigDecimal("1200000"))
                .description("Bàn phím cơ gõ êm, layout TKL")
                .stockQuantity(10)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productVectorService.saveProductVector(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(vectorStore, times(1)).add(anyList());
    }
}
