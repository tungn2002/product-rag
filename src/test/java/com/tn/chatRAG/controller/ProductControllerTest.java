package com.tn.chatRAG.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tn.chatRAG.dto.ProductRequest;
import com.tn.chatRAG.dto.ProductResponse;
import com.tn.chatRAG.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("POST /api/products trả về 201 Created khi request hợp lệ")
    void createProduct_ValidRequest_ReturnsCreated() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Tai nghe Sony WH-1000XM5")
                .price(new BigDecimal("6990000"))
                .description("Chống ồn đỉnh cao")
                .stockQuantity(10)
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .stockQuantity(request.getStockQuantity())
                .build();

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Tạo sản phẩm thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Tai nghe Sony WH-1000XM5"));
    }

    @Test
    @DisplayName("POST /api/products trả về 400 Bad Request khi tên để trống")
    void createProduct_InvalidName_ReturnsBadRequest() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("")
                .price(new BigDecimal("100000"))
                .stockQuantity(5)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data.name").value("Tên sản phẩm không được để trống"));
    }
}
