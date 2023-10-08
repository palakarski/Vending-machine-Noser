package com.example.demo.service.impl;

import static com.example.demo.service.impl.MachineOperationService.calculateAmountToBeReturnInCoins;
import static com.example.demo.service.impl.MachineOperationService.removeInsertedBankNotes;

import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.enumeration.CoinValue;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.dto.InsertBankNoteRequest;
import com.example.demo.model.dto.InsertCoinRequest;
import com.example.demo.model.dto.InsertedMoneyResponse;
import com.example.demo.model.dto.ProductResponse;
import com.example.demo.model.dto.ReturnInsertedMoneyResponse;
import com.example.demo.model.entity.BankNoteEntity;
import com.example.demo.model.entity.CoinEntity;
import com.example.demo.model.entity.MachineEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.BankNoteRepository;
import com.example.demo.repository.CoinRepository;
import com.example.demo.repository.MachineRepository;
import com.example.demo.service.MoneyService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MoneyServiceImpl implements MoneyService {

    private final MachineRepository machineRepository;
    private final CoinRepository coinRepository;
    private final BankNoteRepository bankNoteRepository;
    private final ModelMapper modelMapper;

    @Override
    public InsertedMoneyResponse insertCoins(InsertCoinRequest insertCoinRequest) {
        var machine = getMachineEntity();
        var coinEntity = getCoinEntity(insertCoinRequest.getCoinType());
        incrementCoinCount(machine.getInsertedCoins(), coinEntity);
        machineRepository.save(machine);

        return new InsertedMoneyResponse(machine.getInsertedSum(), convertToProductResponse(machine.getProducts()));
    }

    @Override
    public InsertedMoneyResponse insertBankNotes(InsertBankNoteRequest insertBankNoteRequest) {
        var machine = getMachineEntity();

        if (!machine.getInsertedBankNotes().isEmpty()) {
            throw new BadRequestException("You have already inserted a banknote, which is enough to buy a product.");
        }

        var bankNoteEntity = getBankNoteEntity(insertBankNoteRequest.getBanknoteValue());
        incrementBankNoteCount(machine.getInsertedBankNotes(), bankNoteEntity);
        machineRepository.save(machine);

        return new InsertedMoneyResponse(machine.getInsertedSum(), convertToProductResponse(machine.getProducts()));
    }

    @Override
    public ReturnInsertedMoneyResponse returnInsertedMoney() {
        var machine = getMachineEntity();
        ReturnInsertedMoneyResponse response = new ReturnInsertedMoneyResponse();
        response.setInsertedCoins(convertToCoinResponse(machine.getInsertedCoins()));
        response.setInsertedSum(machine.getInsertedSum());

        machine.getInsertedCoins().clear();

        if (!machine.getInsertedBankNotes().isEmpty()) {
            BigDecimal amountOfBankNotesInserted = calculateBankNoteValue(machine.getInsertedBankNotes());
            removeInsertedBankNotes(machine);
            response.setInsertedBankNotes(calculateAmountToBeReturnInCoins(machineRepository, machine, amountOfBankNotesInserted));
        }

        return response;
    }

    private MachineEntity getMachineEntity() {
        return machineRepository.findFirstById(1L).orElseThrow(() -> new RuntimeException("Machine not found"));
    }

    private CoinEntity getCoinEntity(CoinValue coinType) {
        return coinRepository.findFirstByType(coinType).orElseThrow(() -> new RuntimeException("Coin not found"));
    }

    private BankNoteEntity getBankNoteEntity(BankNoteValue banknoteValue) {
        return bankNoteRepository.findFirstByType(banknoteValue).orElseThrow(() -> new RuntimeException("Banknote not found"));
    }

    private void incrementCoinCount(Map<CoinEntity, Integer> coinMap, CoinEntity coinEntity) {
        coinMap.put(coinEntity, coinMap.getOrDefault(coinEntity, 0) + 1);
    }

    private void incrementBankNoteCount(Map<BankNoteEntity, Integer> bankNoteMap, BankNoteEntity bankNoteEntity) {
        bankNoteMap.put(bankNoteEntity, bankNoteMap.getOrDefault(bankNoteEntity, 0) + 1);
    }

    private Map<CoinValue, Integer> convertToCoinResponse(Map<CoinEntity, Integer> insertedCoins) {
        Map<CoinValue, Integer> insertedCoinResponse = new HashMap<>();
        for (Entry<CoinEntity, Integer> entry : insertedCoins.entrySet()) {
            insertedCoinResponse.put(entry.getKey().getType(), entry.getValue());
        }
        return insertedCoinResponse;
    }

    private List<ProductResponse> convertToProductResponse(List<ProductEntity> productEntities) {
        List<ProductResponse> productResponses = new ArrayList<>();
        for (ProductEntity productEntity : productEntities) {
            productResponses.add(modelMapper.map(productEntity, ProductResponse.class));
        }
        return productResponses;
    }

    private BigDecimal calculateBankNoteValue(Map<BankNoteEntity, Integer> bankNoteEntities) {
        return bankNoteEntities.entrySet().stream()
            .map(entry -> entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())))
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
    }
}
