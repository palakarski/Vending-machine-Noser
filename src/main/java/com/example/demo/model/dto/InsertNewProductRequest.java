package com.example.demo.model.dto;

import com.example.demo.enumeration.ProductSlotType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InsertNewProductRequest {

    private String productType;
    private BigDecimal price;
    private ProductSlotType productSlotType;
}
