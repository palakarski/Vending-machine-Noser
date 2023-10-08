package com.example.demo.service;

import com.example.demo.model.dto.InsertBankNoteRequest;
import com.example.demo.model.dto.InsertCoinRequest;
import com.example.demo.model.dto.InsertedMoneyResponse;
import com.example.demo.model.dto.ReturnInsertedMoneyResponse;

public interface MoneyService {

    InsertedMoneyResponse insertCoins(InsertCoinRequest insertCoinRequest);

    InsertedMoneyResponse insertBankNotes(InsertBankNoteRequest insertBankNoteRequest);

    ReturnInsertedMoneyResponse returnInsertedMoney();
}
