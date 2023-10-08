package com.example.demo.model.dto;

import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.enumeration.CoinValue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatisticResponse {

    private BigDecimal profitSum;
    private Map<CoinValue, Integer> coinsProfit;
    private Map<BankNoteValue, Integer> bankNotesProfit;
    private List<ProductAvailabilityResponse> productAvailability;
}
