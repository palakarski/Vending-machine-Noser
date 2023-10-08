package com.example.demo.controller;

import static com.example.demo.utils.Constants.BASE_URL;
import static com.example.demo.utils.Constants.INSERT_BANKNOTE;
import static com.example.demo.utils.Constants.INSERT_COIN;
import static com.example.demo.utils.Constants.MONEY;
import static com.example.demo.utils.Constants.RETURN_INSERTED_COINS;

import com.example.demo.model.dto.InsertBankNoteRequest;
import com.example.demo.model.dto.InsertCoinRequest;
import com.example.demo.model.dto.InsertedMoneyResponse;
import com.example.demo.model.dto.ReturnInsertedMoneyResponse;
import com.example.demo.service.MoneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = BASE_URL + MONEY)
@AllArgsConstructor
public class InsertMoneyController {

    private final MoneyService moneyService;

    @Operation(
        summary = "Insert a coin",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InsertedMoneyResponse.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PostMapping(value = INSERT_COIN, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InsertedMoneyResponse> insertCoin(@RequestBody InsertCoinRequest insertCoinRequest) {
        return ResponseEntity.ok(moneyService.insertCoins(insertCoinRequest));
    }

    @Operation(
        summary = "Insert a banknote",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InsertedMoneyResponse.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PostMapping(value = INSERT_BANKNOTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InsertedMoneyResponse> insertBanknotes(@RequestBody InsertBankNoteRequest banknotesRequest) {
        return ResponseEntity.ok(moneyService.insertBankNotes(banknotesRequest));
    }

    @Operation(
        summary = "Return inserted money",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReturnInsertedMoneyResponse.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PostMapping(value = RETURN_INSERTED_COINS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReturnInsertedMoneyResponse> returnInsertedMoney() {
        return ResponseEntity.ok(moneyService.returnInsertedMoney());
    }
}
