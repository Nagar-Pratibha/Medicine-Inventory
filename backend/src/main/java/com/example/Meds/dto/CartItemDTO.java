package com.example.Meds.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
//    @NotNull
    private Long cartId;
    @NotNull private Long medicineId;
    @Positive
    private int quantity;
}
