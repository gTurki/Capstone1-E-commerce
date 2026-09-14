package com.example.capstone1ecommercewebsite.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Category {

    @NotEmpty(message = "ID must not be empty")
    @NotBlank(message = "ID must not be blank")
    @Size(min = 3, max = 7, message = "ID must be between 3-7")
    private String id;
    @NotEmpty(message = "Name must not be empty")
    @NotBlank(message = "Name must not be blank")
    private String name;
}
