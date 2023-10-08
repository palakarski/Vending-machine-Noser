package com.example.demo.integration;

import static com.example.demo.enumeration.ProductSlotType.ONE;
import static com.example.demo.enumeration.ProductSlotType.SIXTEEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.controller.InsertMoneyController;
import com.example.demo.controller.MachineSupportController;
import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.enumeration.CoinValue;
import com.example.demo.enumeration.ProductSlotType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.dto.InsertNewProductRequest;
import com.example.demo.model.dto.RemoveProductRequest;
import com.example.demo.model.entity.CoinEntity;
import com.example.demo.model.entity.ProductEntity;
import com.example.demo.repository.MachineRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.MachineSupportService;
import java.math.BigDecimal;
import java.util.Map.Entry;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MachineSupportControllerTest extends BaseForIntegrationTests {

    @Autowired
    private MachineSupportController machineSupportController;

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
    void shouldReturnTheInsertedMoney3() {

        var response = machineSupportController.getStatistic().getBody();

        assertEquals(BigDecimal.valueOf(22.50).stripTrailingZeros(),response.getProfitSum().stripTrailingZeros());
        assertEquals(2,response.getCoinsProfit().size());
        assertEquals(2,response.getBankNotesProfit().size());
        assertEquals(15,response.getProductAvailability().size());
        assertEquals(8,response.getProductAvailability().stream().filter(productAvailabilityResponse -> productAvailabilityResponse.getProductType().equals("MILKA")).findFirst().get().getUnits());
        assertEquals(9,response.getProductAvailability().stream().filter(productAvailabilityResponse -> productAvailabilityResponse.getProductType().equals("KITKAT")).findFirst().get().getUnits());

    }

    @Test
    @Order(2)
    void shouldThrowNotFoundExceptionWhenProductNotFound() {
        var machine = machineRepository.findFirstById(1L).get();
        boolean notFull = false;
        for (Entry<ProductEntity, Integer> entry : machine.getProductForSale().entrySet()) {
            if (!entry.getValue().equals(10)) {
                notFull = true;
                break;
            }
        }
        assertTrue(notFull);

        machineSupportController.updateProductsStock();

        machine = machineRepository.findFirstById(1L).get();
        notFull = false;
        for (Entry<ProductEntity, Integer> entry : machine.getProductForSale().entrySet()) {
            if (!entry.getValue().equals(10)) {
                notFull = true;
                break;
            }
        }
        assertFalse(notFull);


    }

    @Test
    @Order(3)
    void shouldThrowNotFoundExceptionWhen() {
        var machine = machineRepository.findFirstById(1L).get();
        boolean notFull = false;
        for (Entry<CoinEntity, Integer> entry : machine.getCoinsForChange().entrySet()) {
            if (!entry.getValue().equals(50)) {
                notFull = true;
                break;
            }
        }
        assertTrue(notFull);

        machineSupportController.updateCoinsForChange();

        machine = machineRepository.findFirstById(1L).get();
        notFull = false;
        for (Entry<CoinEntity, Integer> entry : machine.getCoinsForChange().entrySet()) {
            if (!entry.getValue().equals(50)) {
                notFull = true;
                break;
            }
        }
        assertFalse(notFull);


    }

    @Test
    @Order(4)
    void shouldThrowNotFoundExceptionWhen2() {
        var machine = machineRepository.findFirstById(1L).get();

        assertFalse(machine.getBankNotesProfit().isEmpty());
        assertFalse(machine.getCoinsProfit().isEmpty());
        assertEquals(BigDecimal.valueOf(22.50).stripTrailingZeros(),machine.getProfitSum().stripTrailingZeros());

        var response = machineSupportController.takeProfit().getBody();

        machine = machineRepository.findFirstById(1L).get();

        assertTrue(machine.getBankNotesProfit().isEmpty());
        assertTrue(machine.getCoinsProfit().isEmpty());
        assertEquals(BigDecimal.ZERO,machine.getProfitSum());

    }

    @Test
    @Order(5)
    void shouldThrowNotFoundExceptionWhen3() {

        var request = new InsertNewProductRequest("CHOCOLATE",BigDecimal.valueOf(2.10), ONE);
        assertThrows(BadRequestException.class, () -> {
            machineSupportController.addNewProduct(request);
        });
        assertThrows(BadRequestException.class, () -> {
            machineSupportController.addNewProduct(request);
        }, "You have already inserted a banknote, which is enough to buy a product.");
    }

    @Test
    @Order(6)
    void shouldReturnTheInsertedMoney() {
        var machine = machineRepository.findFirstById(1L).get();
        var expectedProductForSaleSizeBeforeAddingProduct = 15;
        assertEquals(expectedProductForSaleSizeBeforeAddingProduct,machine.getProductForSale().size());
        assertEquals(expectedProductForSaleSizeBeforeAddingProduct,machine.getProducts().size());

        var request = new InsertNewProductRequest("CHOCOLATE",BigDecimal.valueOf(2.10), SIXTEEN);
        machineSupportController.addNewProduct(request);
        machine = machineRepository.findFirstById(1L).get();

        var expectedProductForSaleSizeAfterAddingProduct = 16;
        var product = machine.getProductForSale().keySet().stream().filter(productEntity -> productEntity.getProductSlot().equals(SIXTEEN))
            .findFirst().get();

        assertEquals(expectedProductForSaleSizeAfterAddingProduct,machine.getProductForSale().size());
        assertEquals(expectedProductForSaleSizeAfterAddingProduct,machine.getProducts().size());
        assertEquals("CHOCOLATE",product.getProductType());
        assertEquals(SIXTEEN,product.getProductSlot());
        assertEquals(BigDecimal.valueOf(2.10).stripTrailingZeros(),product.getPrice().stripTrailingZeros());

    }

    @Test
    @Order(7)
    void shouldReturnTheInsertedMoney2() {
        var machine = machineRepository.findFirstById(1L).get();
        var expectedProductForSaleSizeBeforeAddingProduct = 16;
        assertEquals(expectedProductForSaleSizeBeforeAddingProduct,machine.getProductForSale().size());
        assertEquals(expectedProductForSaleSizeBeforeAddingProduct,machine.getProducts().size());

        var request = new RemoveProductRequest( SIXTEEN);
        machineSupportController.removeProduct(request);
        machine = machineRepository.findFirstById(1L).get();

        var expectedProductForSaleSizeAfterAddingProduct = 15;

        assertEquals(expectedProductForSaleSizeAfterAddingProduct,machine.getProductForSale().size());
        assertEquals(expectedProductForSaleSizeAfterAddingProduct,machine.getProducts().size());
        assertTrue(machine.getProductForSale().keySet().stream().filter(productEntity -> productEntity.getProductSlot().equals(SIXTEEN))
            .findFirst().isEmpty());

    }
}
