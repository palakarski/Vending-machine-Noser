package com.example.demo.controller;

import com.example.demo.model.dto.BuyProductRequest;
import com.example.demo.model.dto.BuyProductResponse;
import com.example.demo.service.BuyingProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.utils.Constants.*;

@RestController
@RequestMapping(value = BASE_URL + BUY_PRODUCT)
@AllArgsConstructor
public class BuyingProductController {

    private final BuyingProductService buyingProductService;

    @Operation(
        summary = "Buy a product",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BuyProductResponse.class))),
            @ApiResponse(responseCode = "422", description = "Request validation problem")
        }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BuyProductResponse> buyProduct(@RequestBody BuyProductRequest buyProductRequest) {
        return ResponseEntity.ok(buyingProductService.buyProduct(buyProductRequest));
    }
}
