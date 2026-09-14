package com.example.capstone1ecommercewebsite.Model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantStock {

    @NotEmpty(message = "ID must not be empty")
    @NotBlank(message = "ID must not be blank")
    @Size(min = 3, max = 7, message = "ID must be between 3-7")
    private String id;
    @NotEmpty(message = "ProductID must not be empty")
    @NotBlank(message = "ProductID must not be blank")
    private String productID;
    @NotEmpty(message = "merchantID must not be empty")
    @NotBlank(message = "merchantID must not be blank")
    private String merchantID;
    @NotNull(message = "Stock cannot be null")
    @Min(value = 10, message = "Stock must be 10 or more at the start")
    private int stock;
}
