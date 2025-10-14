package com.example.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private LocalDate expiryDate;       // hasExpiry=trueのとき必須
    private LocalDateTime purchasedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
