package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InsertedMoneyResponse {

    private BigDecimal insertedSum;
    private List<ProductResponse> productToBuy;
}
