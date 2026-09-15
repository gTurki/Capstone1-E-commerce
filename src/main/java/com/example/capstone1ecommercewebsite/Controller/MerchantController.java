package com.example.capstone1ecommercewebsite.Controller;

import com.example.capstone1ecommercewebsite.Api.ApiResponse;
import com.example.capstone1ecommercewebsite.Model.Merchant;
import com.example.capstone1ecommercewebsite.Service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/get")
    public ResponseEntity<?> getMerchants() {
        return ResponseEntity.status(200).body(merchantService.getMerchants());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMerchant(@RequestBody @Valid Merchant merchant, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        merchantService.addMerchant(merchant);
        return ResponseEntity.status(200).body(new ApiResponse("Merchant added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMerchant(@PathVariable String id, @RequestBody @Valid Merchant merchant, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        boolean isUpdated = merchantService.updateMerchant(id, merchant);
        if (isUpdated)
            return ResponseEntity.status(200).body(new ApiResponse("Merchant updated successfully"));

        return ResponseEntity.status(400).body(new ApiResponse("ID not found"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMerchant(@PathVariable String id) {
        boolean isDeleted = merchantService.deleteMerchant(id);
        if (isDeleted)
            return ResponseEntity.status(200).body(new ApiResponse("Merchant deleted successfully"));

        return ResponseEntity.status(400).body(new ApiResponse("ID not found"));
    }

    @PutMapping("/addstock/productid/{productID}/merchantid/{merchantID}/stock/{stock}")
    public ResponseEntity<?> addStock(@PathVariable String productID, @PathVariable String merchantID, @PathVariable int stock) {
        int addingStatus = merchantService.addStock(productID, merchantID, stock);

        return switch (addingStatus) {
            case 200 -> ResponseEntity.status(200).body(new ApiResponse("Stock increased by " + stock + " for product ID: " + productID));
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("ProductID not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Merchant ID not found"));
            default -> ResponseEntity.status(400).body(new ApiResponse("IDs not added in the merchant stock yet, or merchant doesnt sell that product"));
        };
    }

    @PutMapping("/buy/userid/{userID}/productid/{productID}/merchantid/{merchantID}")
    public ResponseEntity<?> buyProduct(@PathVariable String userID, @PathVariable String productID, @PathVariable String merchantID) {
        int buyingStatus = merchantService.buyProduct(userID, productID, merchantID);

        return switch (buyingStatus) {
            case 200 -> ResponseEntity.status(200).body(new ApiResponse("Purchased successfully"));
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("User does not have enough balance to buy the product"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("User ID not found"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Product ID not found"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Merchant ID not found"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Stock is empty"));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}
