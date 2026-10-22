package com.example.Meds.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long medicineId;
    private String medicineName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal; // price * quantity
}