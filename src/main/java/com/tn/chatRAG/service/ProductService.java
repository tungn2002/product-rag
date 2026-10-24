package com.tn.chatRAG.service;

import com.tn.chatRAG.dto.ProductRequest;
import com.tn.chatRAG.dto.ProductResponse;

public interface ProductService {

    /**
     * Tạo mới một sản phẩm trong hệ thống
     *
     * @param request thông tin sản phẩm cần tạo
     * @return thông tin sản phẩm vừa tạo
     */
    ProductResponse createProduct(ProductRequest request);
}
