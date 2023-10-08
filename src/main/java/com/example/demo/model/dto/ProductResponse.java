package com.example.demo.model.dto;

import com.example.demo.enumeration.ProductSlotType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponse {

    private BigDecimal price;
    private String productType;
    private ProductSlotType productSlot;
}
