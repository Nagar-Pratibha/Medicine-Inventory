package com.example.Meds.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
//    @NotNull
    private Integer userId;
    @NotNull private Long addressId;
}
