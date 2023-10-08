package com.example.demo.service.impl;

import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.enumeration.CoinValue;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.dto.InsertNewProductRequest;
import com.example.demo.model.dto.ProductAvailabilityResponse;
import com.example.demo.model.dto.ProfitResponse;
import com.example.demo.model.dto.RemoveProductRequest;
import com.example.demo.model.dto.StatisticResponse;
import com.example.demo.model.entity.BankNoteEntity;
import com.example.demo.model.entity.CoinEntity;
import com.example.demo.model.entity.MachineEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.MachineRepository;
import com.example.demo.service.MachineSupportService;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MachineSupportServiceImpl implements MachineSupportService {

    private final MachineRepository machineRepository;
    private final ModelMapper modelMapper;

    @Override
    public void updateCoinsForChange() {
        var machine = getMachineEntity();
        var coins = machine.getCoinsForChange();
        coins.replaceAll((coin, quantity) -> 50);
        machineRepository.save(machine);
    }

    @Override
    public void removeProduct(RemoveProductRequest request) {
        var machine = getMachineEntity();
        var productsForSale = machine.getProductForSale();
        var productsInventory = machine.getProducts();

        var productToRemove = productsForSale.keySet().stream()
            .filter(productEntity -> productEntity.getProductSlot().equals(request.getProductSlotType()))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Product not found"));

        productsInventory.remove(productToRemove);
        productsForSale.remove(productToRemove);

        machineRepository.save(machine);
    }

    @Override
    public void addProduct(InsertNewProductRequest insertNewProductRequest) {
        var machine = getMachineEntity();
        var productsForSale = machine.getProductForSale();
        var productsInventory = machine.getProducts();

        var isSlotTaken = productsForSale.keySet().stream()
            .anyMatch(productEntity -> productEntity.getProductSlot().equals(insertNewProductRequest.getProductSlotType()));

        if (isSlotTaken) {
            throw new BadRequestException("Slot is taken, please choose another one");
        }

        ProductEntity product = ProductEntity.builder()
            .price(insertNewProductRequest.getPrice())
            .machine(machine)
            .productSlot(insertNewProductRequest.getProductSlotType())
            .productType(insertNewProductRequest.getProductType())
            .build();

        productsInventory.add(product);
        productsForSale.put(product, 10);

        machineRepository.save(machine);
    }

    @Override
    public void updateProductsStock() {
        var machine = getMachineEntity();
        var productsForSale = machine.getProductForSale();
        productsForSale.replaceAll((productEntity, quantity) -> 10);
        machineRepository.save(machine);
    }

    @Override
    public ProfitResponse takeProfit() {
        var machine = getMachineEntity();
        Map<CoinValue, Integer> coinValueMap = mapCoinEntitiesToValues(machine.getCoinsProfit());
        Map<BankNoteValue, Integer> banknoteValueMap = mapBanknoteEntitiesToValues(machine.getBankNotesProfit());
        ProfitResponse profitResponse = new ProfitResponse(machine.getProfitSum(), coinValueMap, banknoteValueMap);
        machine.getBankNotesProfit().clear();
        machine.getCoinsProfit().clear();
        machineRepository.save(machine);
        return profitResponse;
    }

    @Override
    public StatisticResponse getStatistic() {
        var machine = getMachineEntity();
        var profitSum = machine.getProfitSum();
        Map<CoinValue, Integer> coinValueMap = mapCoinEntitiesToValues(machine.getCoinsProfit());
        Map<BankNoteValue, Integer> banknoteValueMap = mapBanknoteEntitiesToValues(machine.getBankNotesProfit());
        List<ProductAvailabilityResponse> productAvailability = mapProductsToAvailability(machine.getProductForSale());

        return new StatisticResponse(profitSum, coinValueMap, banknoteValueMap, productAvailability);
    }

    private MachineEntity getMachineEntity() {
        return machineRepository.findFirstById(1L).orElseThrow(() -> new RuntimeException("Machine not found"));
    }

    private Map<CoinValue, Integer> mapCoinEntitiesToValues(Map<CoinEntity, Integer> coinEntities) {
        return coinEntities.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().getType(), Entry::getValue));
    }

    private Map<BankNoteValue, Integer> mapBanknoteEntitiesToValues(Map<BankNoteEntity, Integer> banknoteEntities) {
        return banknoteEntities.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().getType(), Entry::getValue));
    }

    private List<ProductAvailabilityResponse> mapProductsToAvailability(Map<ProductEntity, Integer> productEntities) {
        return productEntities.entrySet().stream()
            .map(entry -> {
                ProductAvailabilityResponse productResponse = modelMapper.map(entry.getKey(), ProductAvailabilityResponse.class);
                productResponse.setUnits(entry.getValue());
                return productResponse;
            })
            .collect(Collectors.toList());
    }
}
