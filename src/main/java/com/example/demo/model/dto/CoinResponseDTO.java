package com.example.demo.model.dto;

import com.example.demo.enumeration.CoinValue;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CoinResponseDTO {

    private CoinValue type;
    private BigDecimal value;
}
