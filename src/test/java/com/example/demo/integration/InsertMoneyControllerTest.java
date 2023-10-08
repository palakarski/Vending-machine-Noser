package com.example.demo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.controller.InsertMoneyController;
import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.enumeration.CoinValue;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.dto.InsertBankNoteRequest;
import com.example.demo.model.dto.InsertCoinRequest;
import com.example.demo.repository.MachineRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InsertMoneyControllerTest extends BaseForIntegrationTests {

    @Autowired
    private InsertMoneyController insertMoneyController;

    @Autowired
    private MachineRepository machineRepository;

    @Test
    @Order(1)
    void shouldInsertCoins() {
        var request = new InsertCoinRequest(CoinValue.FIFTY_ST);
        var response = insertMoneyController.insertCoin(request).getBody();
        var machine = machineRepository.findFirstById(1L).get();
        assertEquals(BigDecimal.valueOf(0.50).stripTrailingZeros(), machine.getInsertedSum().stripTrailingZeros());
        assertEquals(response.getInsertedSum().stripTrailingZeros(), machine.getInsertedSum().stripTrailingZeros());
        assertEquals(1, machine.getInsertedCoins().keySet().size());

        request = new InsertCoinRequest(CoinValue.TWO_LV);
        response = insertMoneyController.insertCoin(request).getBody();
        machine = machineRepository.findFirstById(1L).get();
        assertEquals(BigDecimal.valueOf(2.50).stripTrailingZeros(), machine.getInsertedSum().stripTrailingZeros());
        assertEquals(response.getInsertedSum().stripTrailingZeros(), machine.getInsertedSum().stripTrailingZeros());
        assertEquals(2, machine.getInsertedCoins().keySet().size());

    }

    @Test
    @Order(2)
    void shouldInsertBankNote() {
        var request = new InsertBankNoteRequest(BankNoteValue.TEN_LV);
        var response = insertMoneyController.insertBanknotes(request).getBody();
        var machine = machineRepository.findFirstById(1L).get();
        assertEquals(BigDecimal.valueOf(12.50).stripTrailingZeros(), machine.getInsertedSum().stripTrailingZeros());
        assertEquals(response.getInsertedSum().stripTrailingZeros(), machine.getInsertedSum().stripTrailingZeros());
        assertEquals(2, machine.getInsertedCoins().keySet().size());
        assertEquals(1, machine.getInsertedBankNotes().keySet().size());

    }

    @Test
    @Order(3)
    void shouldThrowBadRequestExceptionTryingToPutMoreThanOneBankNote() {
        var request = new InsertBankNoteRequest(BankNoteValue.TEN_LV);

        assertThrows(BadRequestException.class, () -> {
            insertMoneyController.insertBanknotes(request);
        });
        assertThrows(BadRequestException.class, () -> {
            insertMoneyController.insertBanknotes(request);
        }, "You have already inserted a banknote, which is enough to buy a product.");

    }

    @Test
    @Order(4)
    void shouldReturnTheInsertedMoney() {
        var machine = machineRepository.findFirstById(1L).get();
        assertEquals(BigDecimal.valueOf(12.50).stripTrailingZeros(), machine.getInsertedSum().stripTrailingZeros());
        var response = insertMoneyController.returnInsertedMoney().getBody();
        machine = machineRepository.findFirstById(1L).get();
        var coinTwoLv = CoinValue.TWO_LV;
        var coinFiftySt = CoinValue.FIFTY_ST;
        var bankNote = BankNoteValue.FIVE_LV;
        var expectedCoinsForChangeFiftySt = 48;
        var expectedCoinsForChangeTwoLv = 43;
        var expectedBankNoteProfitTenLv = 1;

        var coinEntityTwoLv = machine.getCoinsForChange().keySet().stream().filter(coinEntity -> coinEntity.getType().equals(coinTwoLv))
            .findFirst().get();
        var coinEntityFiftySt = machine.getCoinsForChange().keySet().stream().filter(coinEntity -> coinEntity.getType().equals(coinFiftySt))
            .findFirst().get();
        var bankNoteEntity = machine.getBankNotesProfit().keySet().stream()
            .filter(bankNoteEntity1 -> bankNoteEntity1.getType().equals(bankNote)).findFirst().get();

        assertEquals(BigDecimal.ZERO, machine.getInsertedSum().stripTrailingZeros());

        assertEquals(BigDecimal.valueOf(12.50).stripTrailingZeros(), response.getInsertedSum().stripTrailingZeros());
        assertEquals(expectedBankNoteProfitTenLv, machine.getBankNotesProfit().get(bankNoteEntity));
        assertEquals(expectedCoinsForChangeTwoLv, machine.getCoinsForChange().get(coinEntityTwoLv));
        assertEquals(expectedCoinsForChangeFiftySt, machine.getCoinsForChange().get(coinEntityFiftySt));

    }
}
