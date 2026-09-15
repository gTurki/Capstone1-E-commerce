package com.example.capstone1ecommercewebsite.Service;

import com.example.capstone1ecommercewebsite.Model.Merchant;
import com.example.capstone1ecommercewebsite.Model.MerchantStock;
import com.example.capstone1ecommercewebsite.Model.Product;
import com.example.capstone1ecommercewebsite.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
        for (Merchant m : merchants) {
            if (m.getId().equalsIgnoreCase(id)) {
                merchants.remove(m);
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

        boolean correctUserID = false;
        boolean correctProductID = false;
        boolean correctMerchantID = false;
        double userBalance = 0;
        double productPrice = 0;
        int userIndex = 0;


        for (int i = 0; i < userService.getUsers().size(); i++) {
            if (userService.getUsers().get(i).getId().equalsIgnoreCase(userID)) {
                correctUserID = true;
                userBalance = userService.getUsers().get(i).getBalance();
                userIndex = i;
                break;
            }
        }

        for (Product p : productService.getProducts()) {
            if (p.getId().equalsIgnoreCase(productID)) {
                correctProductID = true;
                productPrice = p.getPrice();
                break;
            }
        }

        if (productPrice > userBalance)
            return 1; // means user does not have enough balance to buy this product

        for (Merchant m : merchants) {
            if (m.getId().equalsIgnoreCase(merchantID)) {
                correctMerchantID = true;
                break;
            }
        }

        if (!correctUserID)
            return 2; // means user ID not found

        if (!correctProductID)
            return 3; // means product ID not found

        if (!correctMerchantID)
            return 4; // means merchant ID not found

        for (MerchantStock ms : merchantStockService.getMerchantStocks()) {
            if (ms.getMerchantID().equalsIgnoreCase(merchantID) &&
                    ms.getProductID().equalsIgnoreCase(productID) ) {

                if (ms.getStock() < 1)
                    return 5; // means stock is empty

                ms.setStock(ms.getStock() - 1);

                // deducting product price from suer balance
                userService.getUsers().get(userIndex).setBalance(
                    userService.getUsers().get(userIndex).getBalance() - productPrice);
                return 200;
            }
        }
        return 6; // means either merchant id or product not found in stock or the merchant do not sell that product
    }
}
