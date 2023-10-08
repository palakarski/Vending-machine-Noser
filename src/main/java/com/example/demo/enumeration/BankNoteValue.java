package com.example.demo.enumeration;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BankNoteValue {
    TEN_LV(BigDecimal.valueOf(10)),
    FIVE_LV(BigDecimal.valueOf(5));

    private BigDecimal value;
}
