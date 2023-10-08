package com.example.demo.model.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductAvailabilityResponse {

    private BigDecimal price;
    private String productType;
    private Integer units;
}
