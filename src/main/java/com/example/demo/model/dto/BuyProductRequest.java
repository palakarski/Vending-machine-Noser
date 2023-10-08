package com.example.demo.model.dto;

import com.example.demo.enumeration.ProductSlotType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuyProductRequest {

    private ProductSlotType slotType;
}
