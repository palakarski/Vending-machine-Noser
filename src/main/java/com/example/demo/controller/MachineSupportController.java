package com.example.demo.controller;

import static com.example.demo.utils.Constants.ADD_NEW_PRODUCTS;
import static com.example.demo.utils.Constants.REFILL_STOCK;
import static com.example.demo.utils.Constants.REMOVE_PRODUCTS;
import static com.example.demo.utils.Constants.STATISTIC;
import static com.example.demo.utils.Constants.SUPPORT;
import static com.example.demo.utils.Constants.TAKE_PROFITS;
import static com.example.demo.utils.Constants.UPDATE_COINS_FOR_CHANGE;

import com.example.demo.model.dto.InsertNewProductRequest;
import com.example.demo.model.dto.ProfitResponse;
import com.example.demo.model.dto.RemoveProductRequest;
import com.example.demo.model.dto.StatisticResponse;
import com.example.demo.service.MachineSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = SUPPORT)
@AllArgsConstructor
public class MachineSupportController {

    private final MachineSupportService machineSupportService;

    @Operation(
        summary = "Refill the stock of products.",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PutMapping(value = REFILL_STOCK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateProductsStock() {
        machineSupportService.updateProductsStock();
    }

    @Operation(
        summary = "Add a new product",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PostMapping(value = ADD_NEW_PRODUCTS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void addNewProduct(@RequestBody InsertNewProductRequest insertNewProductRequest) {
        machineSupportService.addProduct(insertNewProductRequest);
    }

    @Operation(
        summary = "Remove a product",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @DeleteMapping(value = REMOVE_PRODUCTS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeProduct(@RequestBody RemoveProductRequest removeProductRequest) {
        machineSupportService.removeProduct(removeProductRequest);
    }

    @Operation(
        summary = "Update coins for change",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PostMapping(value = UPDATE_COINS_FOR_CHANGE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCoinsForChange() {
        machineSupportService.updateCoinsForChange();
    }

    @Operation(
        summary = "Retrieve profits from the machine",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProfitResponse.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PostMapping(value = TAKE_PROFITS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfitResponse> takeProfit() {
        return ResponseEntity.ok(machineSupportService.takeProfit());
    }

    @Operation(
        summary = "Get statistics about the machine",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StatisticResponse.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @GetMapping(value = STATISTIC, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatisticResponse> getStatistic() {
        return ResponseEntity.ok(machineSupportService.getStatistic());
    }
}
