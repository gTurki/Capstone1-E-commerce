package com.example.capstone1ecommercewebsite.Service;

import com.example.capstone1ecommercewebsite.Model.Merchant;
import com.example.capstone1ecommercewebsite.Model.MerchantStock;
import com.example.capstone1ecommercewebsite.Model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final ProductService productService;
    private final MerchantStockService merchantStockService;

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
}
