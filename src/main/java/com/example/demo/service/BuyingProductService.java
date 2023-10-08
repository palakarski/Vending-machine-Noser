package com.example.demo.service;

import com.example.demo.model.dto.BuyProductRequest;
import com.example.demo.model.dto.BuyProductResponse;

public interface BuyingProductService {

    BuyProductResponse buyProduct(BuyProductRequest buyProductRequest);
}
