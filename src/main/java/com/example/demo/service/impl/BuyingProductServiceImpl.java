package com.example.demo.service.impl;

import static com.example.demo.service.impl.MachineOperationService.calculateAmountToBeReturnInCoins;
import static com.example.demo.service.impl.MachineOperationService.removeInsertedBankNotes;
import static com.example.demo.service.impl.MachineOperationService.removeInsertedCoins;
import static com.example.demo.service.impl.MachineOperationService.removeProduct;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.BuyProductRequest;
import com.example.demo.model.dto.BuyProductResponse;
import com.example.demo.model.dto.ProductResponse;
import com.example.demo.repository.MachineRepository;
import com.example.demo.service.BuyingProductService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BuyingProductServiceImpl implements BuyingProductService {

    private final MachineRepository machineRepository;
    private final ModelMapper modelMapper;

    @Override
    public BuyProductResponse buyProduct(BuyProductRequest buyProductRequest) {
        var vendingMachine = machineRepository.findFirstById(1L)
            .orElseThrow(() -> new NotFoundException("Vending machine not found"));

        var productToBuy = vendingMachine.getProducts().stream()
            .filter(productEntity -> productEntity.getProductSlot().equals(buyProductRequest.getSlotType()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Product not found"));

        var insertedSum = vendingMachine.getInsertedSum();
        var productToBuyPrice = productToBuy.getPrice();
        var currentQuantityOfProduct = vendingMachine.getProductForSale().getOrDefault(productToBuy, 0);

        if (currentQuantityOfProduct <= 0) {
            throw new NotFoundException("Sorry, this product is out of stock");
        }

        var response = new BuyProductResponse();

        if (insertedSum.compareTo(productToBuyPrice) < 0) {
            throw new BadRequestException("Inserted amount is not enough for this product");
        }

        if (insertedSum.compareTo(productToBuyPrice) > 0) {
            var amountToBeReturn = insertedSum.subtract(productToBuyPrice);
            if (amountToBeReturn.compareTo(vendingMachine.getAmountForChange()) > 0) {
                // TODO: Implement logic to refill money from inserted coins
                throw new UnsupportedOperationException("The machine won't give you change,so either put the right amount or call operator");
            }

            var amountToBeReturnInCoins = calculateAmountToBeReturnInCoins(machineRepository, vendingMachine, amountToBeReturn);
            response.setAmountToBeReturnInCoins(amountToBeReturnInCoins);
            response.setAmountToBeReturn(amountToBeReturn);
            response.setProduct(modelMapper.map(productToBuy, ProductResponse.class));

            removeProduct(vendingMachine, productToBuy, currentQuantityOfProduct);
            removeInsertedCoins(vendingMachine);
            removeInsertedBankNotes(vendingMachine);
        }

        if (insertedSum.compareTo(productToBuyPrice) == 0) {
            vendingMachine.getProducts().remove(productToBuy);
            response.setProduct(modelMapper.map(productToBuy, ProductResponse.class));
            removeProduct(vendingMachine, productToBuy, currentQuantityOfProduct);
        }

        machineRepository.save(vendingMachine);
        return response;
    }

}
