package com.example.Meds.dto;

import java.math.BigDecimal;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long orderItemId;
    private String medicineName;
    private int quantity;
    private BigDecimal priceAtPurchase;
}
