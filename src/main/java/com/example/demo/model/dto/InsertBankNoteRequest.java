package com.example.demo.model.dto;

import com.example.demo.enumeration.BankNoteValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InsertBankNoteRequest implements MoneyRequest {

    private BankNoteValue banknoteValue;

    @Override
    public BankNoteValue getMoneyValue() {
        return banknoteValue;
    }
}
