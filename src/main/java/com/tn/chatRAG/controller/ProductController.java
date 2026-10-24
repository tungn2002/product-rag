package com.tn.chatRAG.controller;

import com.tn.chatRAG.dto.ApiResponse;
import com.tn.chatRAG.dto.ProductRequest;
import com.tn.chatRAG.dto.ProductResponse;
import com.tn.chatRAG.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        log.info("Nhận request tạo sản phẩm: {}", request.getName());
        ProductResponse responseData = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(responseData, "Tạo sản phẩm thành công"));
    }
}
