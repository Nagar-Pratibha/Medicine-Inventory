package com.example.Meds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;
}