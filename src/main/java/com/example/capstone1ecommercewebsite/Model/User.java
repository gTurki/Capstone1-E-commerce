package com.example.capstone1ecommercewebsite.Model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    @NotEmpty(message = "ID must not be empty")
    @NotBlank(message = "ID must not be blank")
    @Size(min = 3, max = 7, message = "ID must be between 3-7")
    @Pattern(regexp = "^(u\\d+)+$", message = "User ID must start with u followed by digit")
    private String id;
    @NotEmpty(message = "Username must not be empty")
    @NotBlank(message = "Username must not be blank")
    @Size(min = 5, max = 20, message = "Name must be between 5-20")
    private String username;
    @NotEmpty(message = "Password must not be empty")
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 25, message = "Password must be between 6-25 characters")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
            message = "Password must contain letters and numbers")
    private String password;
    @NotEmpty(message = "Email must not be empty")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email is not in a valid format")
    private String email;
    @NotEmpty(message = "Role must not be empty")
    @NotBlank(message = "Role must not be blank")
    @Pattern(regexp = "^(?i)(Admin|Customer)$", message = "Role must be either 'Admin' or 'Customer'")
    private String role;
    @NotNull(message = "Balance must not be null")
    @Positive(message = "Balance must be positive")
    private double balance;
}
