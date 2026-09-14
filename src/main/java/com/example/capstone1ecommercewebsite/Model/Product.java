package com.example.capstone1ecommercewebsite.Model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {

    @NotEmpty(message = "ID must not be empty")
    @NotBlank(message = "ID must not be blank")
    @Size(min = 3, max = 7, message = "ID must be between 3-7")
    private String id;
    @NotEmpty(message = "Name must not be empty")
    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, max = 20, message = "Name must be between 3-20")
    private String name;
    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    private double price;
    @NotEmpty(message = "CategoryID must not be empty")
    @NotBlank(message = "CategoryID must not be blank")
    @Size(min = 3, max = 7, message = "CategoryID must be between 3-7")
    private String categoryID;
}
