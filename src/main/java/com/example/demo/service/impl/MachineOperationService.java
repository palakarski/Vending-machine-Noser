package com.example.demo.service.impl;

import com.example.demo.enumeration.CoinValue;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.entity.BankNoteEntity;
import com.example.demo.model.entity.CoinEntity;
import com.example.demo.model.entity.MachineEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.MachineRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MachineOperationService {

    private MachineOperationService() {
    }

    public static Map<CoinValue, Integer> calculateAmountToBeReturnInCoins(MachineRepository repository, MachineEntity machine,
        BigDecimal returnAmount) {
        Map<CoinValue, Integer> changeCoins = new HashMap<>();

        var coins = machine.getCoinsForChange();
        CoinEntity[] coinValues = coins.keySet().stream()
            .toArray(CoinEntity[]::new);

        Arrays.sort(coinValues, (a, b) -> b.getValue().compareTo(a.getValue()));

        BigDecimal remainingChange = returnAmount;

        for (CoinEntity coin : coinValues) {
            BigDecimal coinBigDecimalValue = coin.getValue();
            int numberOfCoins = remainingChange.divideToIntegralValue(coinBigDecimalValue).intValue();

            if (numberOfCoins > 0) {
                changeCoins.put(coin.getType(), numberOfCoins);

                remainingChange = remainingChange.subtract(coinBigDecimalValue.multiply(BigDecimal.valueOf(numberOfCoins)));

                removeCoinsFromMachine(machine, coin.getType(), numberOfCoins);
            }
        }

        repository.save(machine);

        return changeCoins;
    }

    public static void removeInsertedCoins(MachineEntity machine) {
        var insertedCoins = machine.getInsertedCoins();
        var coinsProfit = machine.getCoinsProfit();

        for (Entry<CoinEntity, Integer> entry : insertedCoins.entrySet()) {
            coinsProfit.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        machine.getInsertedCoins().clear();
    }

    public static void removeInsertedBankNotes(MachineEntity machine) {
        var insertedBankNotes = machine.getInsertedBankNotes();
        var bankNotesProfit = machine.getBankNotesProfit();

        for (Entry<BankNoteEntity, Integer> entry : insertedBankNotes.entrySet()) {
            bankNotesProfit.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        machine.getInsertedBankNotes().clear();
    }

    public static void removeCoinsFromMachine(MachineEntity machine, CoinValue coinValue, int numberOfCoins) {
        var coins = machine.getCoinsForChange();

        for (CoinEntity coin : coins.keySet()) {
            if (coin.getType().equals(coinValue)) {
                int currentQuantity = coins.get(coin);
                int newQuantity = currentQuantity - numberOfCoins;

                if (newQuantity < 0) {
                    throw new BadRequestException("Not enough money");
                } else {
                    coins.put(coin, newQuantity);
                }
            }
        }
    }

    public static void removeProduct(MachineEntity entity, ProductEntity product, Integer currentQuantity) {
        entity.getProductForSale().put(product, Math.subtractExact(currentQuantity, 1));
    }
}