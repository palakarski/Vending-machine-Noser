package com.example.demo.model.dto;

import com.example.demo.enumeration.CoinValue;
import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnInsertedMoneyResponse {

    private BigDecimal insertedSum;
    private Map<CoinValue, Integer> insertedCoins;
    private Map<CoinValue, Integer> insertedBankNotes;

}
