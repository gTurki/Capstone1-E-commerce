package com.example.capstone1ecommercewebsite.Controller;

import com.example.capstone1ecommercewebsite.Api.ApiResponse;
import com.example.capstone1ecommercewebsite.Model.Merchant;
import com.example.capstone1ecommercewebsite.Model.MerchantStock;
import com.example.capstone1ecommercewebsite.Model.Product;
import com.example.capstone1ecommercewebsite.Service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/add/multi")
    public ResponseEntity<?> addMerchants(@RequestBody List<@Valid Merchant> merchants, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        merchantService.addMerchants(merchants);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Merchants added successfully"));
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

    // Extra end point 1, gets merchant low stock
    @GetMapping("/get/low-stock/{merchantID}/{lowStockAmount}")
    public ResponseEntity<?> getLowStockByMerchant(@PathVariable String merchantID, @PathVariable int lowStockAmount) {
        ArrayList<MerchantStock> lowStocks = merchantService.getLowStockByMerchant(merchantID, lowStockAmount);

        if (lowStocks == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Merchant ID not found"));
        }

        if (lowStocks.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("Merchant with ID: '" + merchantID + "' does not have any stock below " + lowStockAmount));
        }
        return ResponseEntity.status(200).body(lowStocks);
    }

    // Extra end point 2, list the merchant products
    @GetMapping("/get/merchant-products/{merchantID}")
    public ResponseEntity<?> getProductsByMerchant(@PathVariable String merchantID) {
        ArrayList<Product> products = merchantService.getProductsByMerchant(merchantID);

        if (products == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Merchant ID not found"));
        }

        if (products.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("Merchant has no products in stock"));
        }
        return ResponseEntity.status(200).body(products);
    }

    // Extra end point 3, compare merchants selling a product
    @GetMapping("/get/compare-merchants/{productID}")
    public ResponseEntity<?> compareProductMerchants(@PathVariable String productID) {
        ArrayList<MerchantStock> stocks = merchantService.compareMerchantsSameProduct(productID);

        if (stocks == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Product ID not found"));
        }

        if (stocks.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No merchants are currently selling product ID: " + productID));
        }
        return ResponseEntity.status(200).body(stocks);
    }

    // Extra end point 4: get products within user's budget
    @GetMapping("/get/products-within-budget/userid/{userID}")
    public ResponseEntity<?> getAffordableProducts(@PathVariable String userID) {
        ArrayList<Product> affordableProducts = merchantService.getProductsWithingBudget(userID);

        if (affordableProducts == null) {
            return ResponseEntity.status(400).body(new ApiResponse("User ID not found"));
        }

        if (affordableProducts.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No products found within user's budget or currently in stock"));
        }
        return ResponseEntity.status(200).body(affordableProducts);
    }

    // Extra end point 5: transfer balance between users
    @PutMapping("/transfer/senderid/{senderID}/receiverid/{receiverID}/amount/{amount}")
    public ResponseEntity<?> transferBalance(
            @PathVariable String senderID,
            @PathVariable String receiverID,
            @PathVariable double amount) {

        int status = merchantService.transferBalance(senderID, receiverID, amount);

        return switch (status) {
            case 200 -> ResponseEntity.status(200).body(new ApiResponse("Balance transferred successfully"));
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Transfer amount must be greater than zero"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Sender ID not found"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Receiver ID not found"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Cannot transfer balance to the same account"));
            case 5 -> ResponseEntity.status(400).body(new ApiResponse("Sender does not have enough balance"));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }

    // Extra end point 6: calculates the shipping duration
    @GetMapping("/shipping-duration/userid/{userID}/productid/{productID}/merchantid/{merchantID}/user-region/{userRegion}/merchant-region/{merchantRegion}")
    public ResponseEntity<?> calculateShippingDuration(
            @PathVariable String userID,
            @PathVariable String productID,
            @PathVariable String merchantID,
            @PathVariable String userRegion,
            @PathVariable String merchantRegion) {

        int result = merchantService.calculateShippingDuration(userID, productID, merchantID, userRegion, merchantRegion);

        return switch (result) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("User ID not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Merchant ID not found"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Merchant does not sell this product"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Product is out of stock with this merchant"));
            case 200 -> ResponseEntity.status(200).body(new ApiResponse("Estimated shipping duration: 2 business days"));
            case 201 -> ResponseEntity.status(200).body(new ApiResponse("Estimated shipping duration: 5 business days"));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }

    // Extra end point 7: refund item (based on condition)
    @PutMapping("/refund/userid/{userID}/productid/{productID}/merchantid/{merchantID}/condition/{condition}")
    public ResponseEntity<?> refundItem(
            @PathVariable String userID,
            @PathVariable String productID,
            @PathVariable String merchantID,
            @PathVariable String condition) {

        int status = merchantService.refundItem(userID, productID, merchantID, condition);

        return switch (status) {
            case 200 -> ResponseEntity.status(200).body(new ApiResponse("Item refunded successfully. Stock restored and balance updated."));
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("User ID not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Product ID not found"));
            case 3 -> ResponseEntity.status(400).body(new ApiResponse("Merchant stock record not found for this product"));
            case 4 -> ResponseEntity.status(400).body(new ApiResponse("Invalid item condition. Must be 'unused' or 'used'"));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }

    // Extra end point 8: redeem coupon to increase balance
    @PutMapping("/apply-discount/{userID}/{code}")
    public ResponseEntity<?> redeemCoupon(@PathVariable String userID, @PathVariable String code) {
        int status = merchantService.redeemCoupon(userID, code);

        return switch (status) {
            case 200 -> ResponseEntity.status(200).body(new ApiResponse("Coupon applied successfully! Balance updated."));
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("User ID not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Invalid or expired coupon code"));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}
