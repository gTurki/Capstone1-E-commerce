package com.example.capstone1ecommercewebsite.Service;

import com.example.capstone1ecommercewebsite.Model.Merchant;
import com.example.capstone1ecommercewebsite.Model.MerchantStock;
import com.example.capstone1ecommercewebsite.Model.Product;
import com.example.capstone1ecommercewebsite.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final ProductService productService;
    private final MerchantStockService merchantStockService;
    private final UserService userService;

    private final ArrayList<Merchant> merchants = new ArrayList<>();

    public ArrayList<Merchant> getMerchants() {
        return merchants;
    }

    public void addMerchant(Merchant merchant) {
        merchants.add(merchant);
    }

    public void addMerchants(List<Merchant> newMerchants) {
        merchants.addAll(newMerchants);
    }

    public boolean updateMerchant(String id, Merchant merchant) {
        for (int i = 0; i < merchants.size(); i++) {
            if (merchants.get(i).getId().equalsIgnoreCase(id)) {
                merchants.set(i, merchant);
                return true;
            }
        }
        return false;
    }

    public boolean deleteMerchant(String id) {
        for (int i = 0; i < merchants.size(); i++) {
            if (merchants.get(i).getId().equalsIgnoreCase(id)) {
                merchants.remove(i);
                return true;
            }
        }
        return false;
    }

    public int addStock(String productID, String merchantID, int stock) {
        boolean correctProductID = false;
        boolean correctMerchantID = false;

        for (Product p : productService.getProducts()) {
            if (p.getId().equalsIgnoreCase(productID)) {
                correctProductID = true;
                break;
            }
        }

        for (Merchant m : merchants) {
            if (m.getId().equalsIgnoreCase(merchantID)) {
                correctMerchantID = true;
                break;
            }
        }

        if (!correctProductID)
            return 1; // means productID does not exist

        if (!correctMerchantID)
            return 2; // means merchantID does not exist

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getProductID().equalsIgnoreCase(productID) && ms.getMerchantID().equalsIgnoreCase(merchantID)) {
                ms.setStock(ms.getStock() + stock);
                return 200;
            }
        }
        return 3; // 3 means that it didn't find either productID or merchantID in merchantStock
    }

    public int buyProduct(String userID, String productID, String merchantID) {

        User targetUser = null;
        Product targetProduct = null;
        boolean merchantExists = false;

        for (User u : userService.getUsers()) {
            if (u.getId().equalsIgnoreCase(userID)) {
                targetUser = u;
                break;
            }
        }
        if (targetUser == null) return 2; // means user ID not found

        for (Product p : productService.getProducts()) {
            if (p.getId().equalsIgnoreCase(productID)) {
                targetProduct = p;
                break;
            }
        }
        if (targetProduct == null) return 3; // means product ID not found
        
        for (Merchant m : merchants) {
            if (m.getId().equalsIgnoreCase(merchantID)) {
                merchantExists = true;
                break;
            }
        }
        if (!merchantExists) return 4; // means merchant ID not found

        if (targetUser.getBalance() < targetProduct.getPrice()) return 1; // means user doesnt have enough balance

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getMerchantID().equalsIgnoreCase(merchantID) &&
                    ms.getProductID().equalsIgnoreCase(productID) ) {

                if (ms.getStock() < 1) return 5; // means stock is empty

                ms.setStock(ms.getStock() - 1);

                // deducting product price from suer balance
                targetUser.setBalance(targetUser.getBalance() - targetProduct.getPrice());
                return 200;
            }
        }
        return 6; // means either merchant id or product not found in stock or the merchant do not sell that product
    }

    public ArrayList<MerchantStock> getLowStockByMerchant(String merchantID, int lowStockAmount) {
        boolean merchantExists = false;

        // checks if merchant exists
        for (Merchant m : merchants) {
            if (m.getId().equalsIgnoreCase(merchantID)) {
                merchantExists = true;
                break;
            }
        }

        if (!merchantExists) {
            return null;
        }

        ArrayList<MerchantStock> lowStockList = new ArrayList<>();

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getMerchantID().equalsIgnoreCase(merchantID) && ms.getStock() <= lowStockAmount) {
                lowStockList.add(ms);
            }
        }
        return lowStockList;
    }

    public ArrayList<Product> getProductsByMerchant(String merchantID) {
        boolean merchantExists = false;

        // checks if merchant exists in merchant stock
        for (Merchant m : merchants) {
            if (m.getId().equalsIgnoreCase(merchantID)) {
                merchantExists = true;
                break;
            }
        }

        if (!merchantExists) {
            return null;
        }

        ArrayList<Product> temp = new ArrayList<>();

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getMerchantID().equalsIgnoreCase(merchantID)) {
                for (Product p : productService.getProducts()) {
                    if (p.getId().equalsIgnoreCase(ms.getProductID())) {
                        // Checks if the product is already added so we dont get duplicates
                        if (!temp.contains(p)) {
                            temp.add(p);
                        }
                        break;
                    }
                }
            }
        }
        return temp;
    }

    public ArrayList<MerchantStock> compareMerchantsSameProduct(String productID) {
        boolean productExists = false;

        // checks if product exists
        for (Product p : productService.getProducts()) {
            if (p.getId().equalsIgnoreCase(productID)) {
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            return null;
        }

        ArrayList<MerchantStock> temp = new ArrayList<>();

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getProductID().equalsIgnoreCase(productID)) {
                temp.add(ms);
            }
        }
        return temp;
    }

    public ArrayList<Product> getProductsWithingBudget(String userID) {
        User targetUser = null;

        // checks user exists
        for (User u : userService.getUsers()) {
            if (u.getId().equalsIgnoreCase(userID)) {
                targetUser = u;
                break;
            }
        }

        if (targetUser == null) {
            return null;
        }

        ArrayList<Product> productsWithinBudget = new ArrayList<>();

        // checks the products within budget and check the stock
        for (Product p : productService.getProducts()) {
            if (p.getPrice() <= targetUser.getBalance()) {

                // check if at least one merchant have the product with stock > 0
                for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
                    if (ms.getProductID().equalsIgnoreCase(p.getId()) && ms.getStock() > 0) {
                        if (!productsWithinBudget.contains(p)) {
                            productsWithinBudget.add(p);
                        }
                        break;
                    }
                }
            }
        }
        return productsWithinBudget;
    }

    public int transferBalance(String senderID, String receiverID, double amount) {
        if (amount <= 0) {
            return 1; // Invalid transfer amount
        }

        User sender = null;
        User receiver = null;

        for (User u : userService.getUsers()) {
            if (u.getId().equalsIgnoreCase(senderID)) {
                sender = u;
            }
            if (u.getId().equalsIgnoreCase(receiverID)) {
                receiver = u;
            }
        }

        if (sender == null) {
            return 2; // Sender ID not found
        }
        if (receiver == null) {
            return 3; // Receiver ID not found
        }
        if (sender.getId().equalsIgnoreCase(receiver.getId())) {
            return 4; // Cannot transfer balance to yourself
        }
        if (sender.getBalance() < amount) {
            return 5; // Insufficient balance
        }

        // Process transfer
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        return 200; // Success
    }

    public int calculateShippingDuration(String userID, String productID, String merchantID, String userRegion, String merchantRegion) {
        User targetUser = null;
        Merchant targetMerchant = null;
        MerchantStock targetStock = null;

        for (User u : userService.getUsers()) {
            if (u.getId().equalsIgnoreCase(userID)) {
                targetUser = u;
                break;
            }
        }
        if (targetUser == null) return 1; // User ID not found


        for (Merchant m : merchants) {
            if (m.getId().equalsIgnoreCase(merchantID)) {
                targetMerchant = m;
                break;
            }
        }
        if (targetMerchant == null) return 2; // Merchant ID not found

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getMerchantID().equalsIgnoreCase(merchantID) && ms.getProductID().equalsIgnoreCase(productID)) {
                targetStock = ms;
                break;
            }
        }
        if (targetStock == null) return 3; // Merchant does not sell this product

        if (targetStock.getStock() <= 0) return 4; // Item out of stock


        if (userRegion.equalsIgnoreCase(merchantRegion)) {
            return 200; // means they are in the same region -> 2 days
        } else {
            return 201; // means they are in different regions -> 5 days
        }
    }

    public int refundItem(String userID, String productID, String merchantID, String condition) {
        User targetUser = null;
        Product targetProduct = null;
        MerchantStock targetStock = null;

        for (User u : userService.getUsers()) {
            if (u.getId().equalsIgnoreCase(userID)) {
                targetUser = u;
                break;
            }
        }
        if (targetUser == null) return 1; // User ID not found

        for (Product p : productService.getProducts()) {
            if (p.getId().equalsIgnoreCase(productID)) {
                targetProduct = p;
                break;
            }
        }
        if (targetProduct == null) return 2; // Product ID not found

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getMerchantID().equalsIgnoreCase(merchantID) && ms.getProductID().equalsIgnoreCase(productID)) {
                targetStock = ms;
                break;
            }
        }
        if (targetStock == null) return 3; // Merchant or Product not found in stock

        // checks the condition and calculate refund
        double refundPercentage;
        if (condition.equalsIgnoreCase("unused")) {
            refundPercentage = 0.80; // 80% refund for unused items
        } else if (condition.equalsIgnoreCase("used")) {
            refundPercentage = 0.50; // 50% refund for used items
        } else {
            return 4; // means invalid condition passed
        }

        double refundAmount = targetProduct.getPrice() * refundPercentage;
        targetUser.setBalance(targetUser.getBalance() + refundAmount);


        targetStock.setStock(targetStock.getStock() + 1);

        return 200; // means everything worked correctly
    }

    public int redeemCoupon(String userID, String code) {
        User targetUser = null;

        for (User u : userService.getUsers()) {
            if (u.getId().equalsIgnoreCase(userID)) {
                targetUser = u;
                break;
            }
        }

        if (targetUser == null) {
            return 1; // User ID not found
        }

        double creditAmount = 0.0;

        if (code.equalsIgnoreCase("student10")) {
            creditAmount = 10.0;
        } else if (code.equalsIgnoreCase("riyadh20")) {
            creditAmount = 20.0;
        } else if (code.equalsIgnoreCase("tuwaiq50")) {
            creditAmount = 50.0;
        } else {
            return 2; // invalid coupon
        }

        targetUser.setBalance(targetUser.getBalance() + creditAmount);

        return 200;
    }
}
