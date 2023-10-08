package com.example.demo.enumeration;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoinValue {

    TEN_ST(BigDecimal.valueOf(0.10)),
    TWENTY_ST(BigDecimal.valueOf(0.20)),
    FIFTY_ST(BigDecimal.valueOf(0.50)),
    ONE_LV(BigDecimal.valueOf(1)),
    TWO_LV(BigDecimal.valueOf(2));

    private BigDecimal value;
}
