package com.example.demo.model.dto;

import com.example.demo.enumeration.CoinValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InsertCoinRequest implements MoneyRequest {

    private CoinValue coinType;

    @Override
    public CoinValue getMoneyValue() {
        return coinType;
    }
}
