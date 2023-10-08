package com.example.demo.utils;

import static com.example.demo.enumeration.ProductSlotType.EIGHT;
import static com.example.demo.enumeration.ProductSlotType.ELEVEN;
import static com.example.demo.enumeration.ProductSlotType.FIFTEEN;
import static com.example.demo.enumeration.ProductSlotType.FIVE;
import static com.example.demo.enumeration.ProductSlotType.FOUR;
import static com.example.demo.enumeration.ProductSlotType.FOURTEEN;
import static com.example.demo.enumeration.ProductSlotType.NINE;
import static com.example.demo.enumeration.ProductSlotType.ONE;
import static com.example.demo.enumeration.ProductSlotType.SEVEN;
import static com.example.demo.enumeration.ProductSlotType.SIX;
import static com.example.demo.enumeration.ProductSlotType.TEN;
import static com.example.demo.enumeration.ProductSlotType.THIRTEEN;
import static com.example.demo.enumeration.ProductSlotType.THREE;
import static com.example.demo.enumeration.ProductSlotType.TWELVE;
import static com.example.demo.enumeration.ProductSlotType.TWO;

import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.enumeration.CoinValue;
import com.example.demo.enumeration.ProductSlotType;
import com.example.demo.model.entity.BankNoteEntity;
import com.example.demo.model.entity.CoinEntity;
import com.example.demo.model.entity.MachineEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.MachineRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostConstructMachine {

    private final MachineRepository machineRepository;
    private static final Map<ProductSlotType, Map<String, Double>> productPrices;

    static {
        Map<ProductSlotType, Map<String, Double>> prices = new HashMap<>();

        prices.put(ONE, Map.of("MILKA", 3.00));
        prices.put(TWO, Map.of("KITKAT", 1.50));
        prices.put(THREE, Map.of("OREO", 2.30));
        prices.put(FOUR, Map.of("SNICKERS", 1.90));
        prices.put(FIVE, Map.of("DORITOS", 2.50));
        prices.put(SIX, Map.of("COCA-COLA", 1.30));
        prices.put(SEVEN, Map.of("PEPSI", 1.30));
        prices.put(EIGHT, Map.of("PRINGLES", 2.80));
        prices.put(NINE, Map.of("PEANUT BUTTER", 3.50));
        prices.put(TEN, Map.of("APPLES", 0.80));
        prices.put(ELEVEN, Map.of("BANANAS", 0.50));
        prices.put(TWELVE, Map.of("GRAPES", 1.90));
        prices.put(THIRTEEN, Map.of("ORANGES", 0.90));
        prices.put(FOURTEEN, Map.of("CHEESE", 5.00));
        prices.put(FIFTEEN, Map.of("YOGURT", 2.70));

        // Create an immutable map
        productPrices = Collections.unmodifiableMap(prices);
    }

    @PostConstruct
    private void createMachine() {
        var entity = MachineEntity.builder()
            .build();

        generateBankNoteEntity(entity);
        generateProducts(entity);
        generateCoinsForChange(entity);
        machineRepository.save(entity);
    }

    private void generateBankNoteEntity(MachineEntity machine) {
        List<BankNoteEntity> banknoteEntities = new ArrayList<>();
        for (BankNoteValue bankNoteValue : BankNoteValue.values()) {
            BankNoteEntity banknote = BankNoteEntity.builder()
                .value(bankNoteValue.getValue())
                .type(bankNoteValue)
                .machine(machine)
                .build();
            banknoteEntities.add(banknote);
        }
        machine.setBanknotes(banknoteEntities);
    }

    private void generateCoinsForChange(MachineEntity machine) {
        List<CoinEntity> coinEntities = new ArrayList<>();
        Map<CoinEntity, Integer> coins = new HashMap<>();
        for (CoinValue coinValue : CoinValue.values()) {
            CoinEntity coin = CoinEntity.builder()
                .value(coinValue.getValue())
                .type(coinValue)
                .machine(machine)
                .build();
            coinEntities.add(coin);
            coins.put(coin, 50);
        }
        machine.setCoins(coinEntities);
        machine.setCoinsForChange(coins);
    }

    private void generateProducts(MachineEntity machine) {
        List<ProductEntity> products = new ArrayList<>();
        Map<ProductEntity, Integer> productsForSale = new HashMap<>();

        for (Map.Entry<ProductSlotType, Map<String, Double>> entry : productPrices.entrySet()) {
            ProductSlotType slot = entry.getKey();
            var productEntrySet = entry.getValue().entrySet();
            ProductEntity productEntity = ProductEntity.builder()
                .productType(productEntrySet.stream().findFirst().get().getKey())
                .price(BigDecimal.valueOf(productEntrySet.stream().findFirst().get().getValue()))
                .productSlot(slot)
                .machine(machine)
                .build();
            products.add(productEntity);
            productsForSale.put(productEntity, 10);

        }

        machine.setProducts(products);
        machine.setProductForSale(productsForSale);

    }
}
