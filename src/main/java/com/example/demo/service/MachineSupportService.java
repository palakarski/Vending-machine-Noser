package com.example.demo.service;

import com.example.demo.model.dto.InsertNewProductRequest;
import com.example.demo.model.dto.ProfitResponse;
import com.example.demo.model.dto.RemoveProductRequest;
import com.example.demo.model.dto.StatisticResponse;

public interface MachineSupportService {

    void removeProduct(RemoveProductRequest productType);

    void addProduct(InsertNewProductRequest insertNewProductRequest);

    void updateProductsStock();

    void updateCoinsForChange();

    ProfitResponse takeProfit();

    StatisticResponse getStatistic();
}
