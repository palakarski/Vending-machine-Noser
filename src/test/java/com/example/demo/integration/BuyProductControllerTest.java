package com.example.demo.integration;

import static com.example.demo.enumeration.ProductSlotType.ONE;
import static com.example.demo.enumeration.ProductSlotType.TWO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.controller.BuyingProductController;
import com.example.demo.controller.InsertMoneyController;
import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.enumeration.CoinValue;
import com.example.demo.enumeration.ProductSlotType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.BuyProductRequest;
import com.example.demo.model.dto.InsertBankNoteRequest;
import com.example.demo.model.dto.InsertCoinRequest;
import com.example.demo.repository.MachineRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.MachineSupportService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuyProductControllerTest extends BaseForIntegrationTests {

    @Autowired
    private BuyingProductController buyingProductController;

    @Autowired
    private InsertMoneyController insertMoneyController;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MachineSupportService machineSupportService;

    @Test
    @Order(1)
    void shouldThrowNotFoundExceptionWhenProductNotFound() {
        var request = new BuyProductRequest(ProductSlotType.SIXTEEN);

        assertThrows(NotFoundException.class, () -> {
            buyingProductController.buyProduct(request);
        });
        assertThrows(NotFoundException.class, () -> {
            buyingProductController.buyProduct(request);
        }, "Product not found");

    }

    @Test
    @Order(2)
    void shouldThrowNotFoundExceptionWhenProductIsOutOfStock() {

        var request = new BuyProductRequest(ONE);
        var product = productRepository.findFirstByProductSlot(ONE).get();
        var machine = machineRepository.findFirstById(1L).get();
        machine.getProductForSale().replace(product, 0);
        machineRepository.save(machine);
        assertThrows(NotFoundException.class, () -> {
            buyingProductController.buyProduct(request);
        });
        assertThrows(NotFoundException.class, () -> {
            buyingProductController.buyProduct(request);
        }, "Sorry, this product is out of stock");
        machine.getProductForSale().replace(product, 10);
        machineRepository.save(machine);
    }

    @Test
    @Order(3)
    void shouldThrowBadRequestExceptionWhenInsertedAmountIsNotEnough() {
        var request = new BuyProductRequest(ONE);

        assertThrows(BadRequestException.class, () -> {
            buyingProductController.buyProduct(request);
        });
        assertThrows(BadRequestException.class, () -> {
            buyingProductController.buyProduct(request);
        }, "Inserted amount is not enough for this product");

    }

    @Test
    @Order(4)
    void shouldThrowUnsupportedOperationExceptionWhenCoinsForChangeAreNotEnough() {

        var request = new BuyProductRequest(ONE);

        var machine = machineRepository.findFirstById(1L).get();
        machine.getCoinsForChange().entrySet().forEach(entry -> entry.setValue(0));
        machineRepository.save(machine);
        insertMoneyController.insertBanknotes(new InsertBankNoteRequest(BankNoteValue.FIVE_LV));
        assertThrows(UnsupportedOperationException.class, () -> {
            buyingProductController.buyProduct(request);
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            buyingProductController.buyProduct(request);
        }, "The machine won't give you change,so either put the right amount or call operator");

        machine.getInsertedBankNotes().clear();
        machineRepository.save(machine);
        machineSupportService.updateCoinsForChange();

    }

    @Test
    @Order(5)
    void buyProdushouldBuyProductSlotOneCase1ctSlotOneCase1() {

        var request = new BuyProductRequest(ONE);
        var product = productRepository.findFirstByProductSlot(ONE).get();
        insertMoneyController.insertBanknotes(new InsertBankNoteRequest(BankNoteValue.FIVE_LV));
        var response = buyingProductController.buyProduct(request).getBody();
        var machine = machineRepository.findFirstById(1L).get();
        var coin = CoinValue.TWO_LV;
        var bankNote = BankNoteValue.FIVE_LV;
        var expectedProductSlot = ONE;
        var expectedProductPrice = BigDecimal.valueOf(3.00);
        var expectedProductType = "MILKA";
        var expectedChangeReturn = BigDecimal.valueOf(2);
        var expectedChangeReturnCoinMap = new HashMap<CoinValue, Integer>(Map.of(CoinValue.TWO_LV, 1));
        var expectedRemainProductQuantityAtSlotOne = 9;
        var expectedCoinsForChange = 49;
        var expectedBankNoteProfit = 1;
        var coinEntity = machine.getCoinsForChange().keySet().stream().filter(coinEntity1 -> coinEntity1.getType().equals(coin)).findFirst()
            .get();
        var bankNoteEntity = machine.getBankNotesProfit().keySet().stream()
            .filter(bankNoteEntity1 -> bankNoteEntity1.getType().equals(bankNote)).findFirst().get();
        var productEntity = machine.getProductForSale().keySet().stream()
            .filter(productEntity1 -> productEntity1.getProductType().equals(product.getProductType())).findFirst().get();

        assertEquals(expectedProductSlot, response.getProduct().getProductSlot());
        assertEquals(expectedProductPrice.stripTrailingZeros(), response.getProduct().getPrice().stripTrailingZeros());
        assertEquals(expectedProductType, response.getProduct().getProductType());
        assertEquals(expectedChangeReturn.stripTrailingZeros(), response.getAmountToBeReturn().stripTrailingZeros());
        assertEquals(response.getAmountToBeReturnInCoins().keySet(), expectedChangeReturnCoinMap.keySet());
        assertTrue(response.getAmountToBeReturnInCoins().entrySet().stream()
            .allMatch(entry -> expectedChangeReturnCoinMap.containsKey(entry.getKey()) && Objects.equals(entry.getValue(),
                expectedChangeReturnCoinMap.get(entry.getKey()))));
        assertEquals(expectedRemainProductQuantityAtSlotOne, machine.getProductForSale().get(productEntity));
        assertEquals(expectedCoinsForChange, machine.getCoinsForChange().get(coinEntity));

        assertEquals(expectedBankNoteProfit, machine.getBankNotesProfit().get(bankNoteEntity));
        assertFalse(machine.getBankNotesProfit().isEmpty());

    }

    @Test
    @Order(6)
    void shouldBuyProductSlotOneCase2() {

        var request = new BuyProductRequest(ONE);
        var product = productRepository.findFirstByProductSlot(ONE).get();
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.FIFTY_ST));
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.FIFTY_ST));
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.FIFTY_ST));
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.TWO_LV));
        var response = buyingProductController.buyProduct(request).getBody();
        var machine = machineRepository.findFirstById(1L).get();
        var coinTwoLv = CoinValue.TWO_LV;
        var coinFiftySt = CoinValue.FIFTY_ST;
        var bankNote = BankNoteValue.FIVE_LV;
        var expectedProductSlot = ONE;
        var expectedProductPrice = BigDecimal.valueOf(3.00);
        var expectedProductType = "MILKA";
        var expectedChangeReturn = BigDecimal.valueOf(0.50);
        var expectedChangeReturnCoinMap = new HashMap<CoinValue, Integer>(Map.of(CoinValue.FIFTY_ST, 1));
        var expectedRemainProductQuantityAtSlotOne = 8;
        var expectedCoinsForChange = 49;
        var expectedBankNoteProfit = 1;
        var expectedCoinProfitTwoLv = 1;
        var expectedCoinProfitFiftySt = 3;
        var coinEntityTwoLv = machine.getCoinsForChange().keySet().stream().filter(coinEntity -> coinEntity.getType().equals(coinTwoLv))
            .findFirst().get();
        var coinEntityFiftySt = machine.getCoinsForChange().keySet().stream().filter(coinEntity -> coinEntity.getType().equals(coinFiftySt))
            .findFirst().get();
        var bankNoteEntity = machine.getBankNotesProfit().keySet().stream()
            .filter(bankNoteEntity1 -> bankNoteEntity1.getType().equals(bankNote)).findFirst().get();
        var productEntity = machine.getProductForSale().keySet().stream()
            .filter(productEntity1 -> productEntity1.getProductType().equals(product.getProductType())).findFirst().get();

        assertEquals(expectedProductSlot, response.getProduct().getProductSlot());
        assertEquals(expectedProductPrice.stripTrailingZeros(), response.getProduct().getPrice().stripTrailingZeros());
        assertEquals(expectedProductType, response.getProduct().getProductType());
        assertEquals(expectedChangeReturn.stripTrailingZeros(), response.getAmountToBeReturn().stripTrailingZeros());
        assertEquals(expectedChangeReturnCoinMap.keySet(), response.getAmountToBeReturnInCoins().keySet());
        assertTrue(response.getAmountToBeReturnInCoins().entrySet().stream()
            .allMatch(entry -> expectedChangeReturnCoinMap.containsKey(entry.getKey()) && Objects.equals(entry.getValue(),
                expectedChangeReturnCoinMap.get(entry.getKey()))));
        assertEquals(expectedRemainProductQuantityAtSlotOne, machine.getProductForSale().get(productEntity));
        assertEquals(expectedCoinsForChange, machine.getCoinsForChange().get(coinEntityFiftySt));

        assertEquals(expectedBankNoteProfit, machine.getBankNotesProfit().get(bankNoteEntity));
        assertEquals(expectedCoinProfitFiftySt, machine.getCoinsProfit().get(coinEntityFiftySt));
        assertEquals(expectedCoinProfitTwoLv, machine.getCoinsProfit().get(coinEntityTwoLv));
        assertFalse(machine.getBankNotesProfit().isEmpty());

    }

    @Test
    @Order(7)
    void shouldBuyProductSlotTwoCase1() {

        var request = new BuyProductRequest(TWO);
        var product = productRepository.findFirstByProductSlot(TWO).get();
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.FIFTY_ST));
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.FIFTY_ST));
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.FIFTY_ST));
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.FIFTY_ST));
        insertMoneyController.insertCoin(new InsertCoinRequest(CoinValue.TWO_LV));
        var response = buyingProductController.buyProduct(request).getBody();
        var machine = machineRepository.findFirstById(1L).get();
        var coinTwoLv = CoinValue.TWO_LV;
        var coinFiftySt = CoinValue.FIFTY_ST;
        var bankNote = BankNoteValue.FIVE_LV;
        var expectedProductSlot = TWO;
        var expectedProductPrice = BigDecimal.valueOf(1.50);
        var expectedProductType = "KITKAT";
        var expectedChangeReturn = BigDecimal.valueOf(2.50);
        var expectedChangeReturnCoinMap = new HashMap<CoinValue, Integer>(Map.of(CoinValue.TWO_LV, 1, CoinValue.FIFTY_ST, 1));
        var expectedRemainProductQuantityAtSlotTwo = 9;
        var expectedCoinsForChangeFiftyLv = 48;
        var expectedCoinsForChangeTwoLv = 48;
        var expectedBankNoteProfit = 1;
        var expectedCoinProfitTwoLv = 2;
        var expectedCoinProfitFiftySt = 7;
        var coinEntityTwoLv = machine.getCoinsForChange().keySet().stream().filter(coinEntity -> coinEntity.getType().equals(coinTwoLv))
            .findFirst().get();
        var coinEntityFiftySt = machine.getCoinsForChange().keySet().stream().filter(coinEntity -> coinEntity.getType().equals(coinFiftySt))
            .findFirst().get();
        var bankNoteEntity = machine.getBankNotesProfit().keySet().stream()
            .filter(bankNoteEntity1 -> bankNoteEntity1.getType().equals(bankNote)).findFirst().get();
        var productEntity = machine.getProductForSale().keySet().stream()
            .filter(productEntity1 -> productEntity1.getProductType().equals(product.getProductType())).findFirst().get();

        assertEquals(expectedProductSlot, response.getProduct().getProductSlot());
        assertEquals(expectedProductPrice.stripTrailingZeros(), response.getProduct().getPrice().stripTrailingZeros());
        assertEquals(expectedProductType, response.getProduct().getProductType());
        assertEquals(expectedChangeReturn.stripTrailingZeros(), response.getAmountToBeReturn().stripTrailingZeros());
        assertEquals(expectedChangeReturnCoinMap.keySet(), response.getAmountToBeReturnInCoins().keySet());
        assertTrue(response.getAmountToBeReturnInCoins().entrySet().stream()
            .allMatch(entry -> expectedChangeReturnCoinMap.containsKey(entry.getKey()) && Objects.equals(entry.getValue(),
                expectedChangeReturnCoinMap.get(entry.getKey()))));
        assertEquals(expectedRemainProductQuantityAtSlotTwo, machine.getProductForSale().get(productEntity));
        assertEquals(expectedCoinsForChangeFiftyLv, machine.getCoinsForChange().get(coinEntityFiftySt));
        assertEquals(expectedCoinsForChangeTwoLv, machine.getCoinsForChange().get(coinEntityTwoLv));

        assertEquals(expectedBankNoteProfit, machine.getBankNotesProfit().get(bankNoteEntity));
        assertEquals(expectedCoinProfitFiftySt, machine.getCoinsProfit().get(coinEntityFiftySt));
        assertEquals(expectedCoinProfitTwoLv, machine.getCoinsProfit().get(coinEntityTwoLv));
        assertFalse(machine.getBankNotesProfit().isEmpty());

    }
}
