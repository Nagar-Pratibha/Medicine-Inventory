package com.example.Meds.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed to Long for consistency

    @Column(name="name", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description; // Field added

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    private Integer stock; // Field added

    @Column(name="manufacture_name")
    private String manufacture_name;

    @Column(name="pack_size")
    private String pack_size;

    @Column(name = "composition_text", columnDefinition = "TEXT")
    private String compositionText;

}
