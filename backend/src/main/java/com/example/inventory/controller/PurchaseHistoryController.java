package com.example.inventory.controller;

import com.example.inventory.entity.PurchaseHistory;
import com.example.inventory.service.PurchaseHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/purchaseHistories")
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    public PurchaseHistoryController(PurchaseHistoryService purchaseHistoryService) {
        this.purchaseHistoryService = purchaseHistoryService;
    }

    @GetMapping
    public List<PurchaseHistory> getAll() {
        return purchaseHistoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseHistory> getById(@PathVariable Long id) {
        Optional<PurchaseHistory> purchaseHistory = purchaseHistoryService.findById(id);
        return purchaseHistory.map(ResponseEntity::ok)
                             .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PurchaseHistory create(@RequestBody PurchaseHistory purchaseHistory) {
        return purchaseHistoryService.save(purchaseHistory);
    }

    @PutMapping("/{id}")
    public PurchaseHistory update(@PathVariable Long id, @RequestBody PurchaseHistory purchaseHistory) {
        purchaseHistory.setId(id);
        return purchaseHistoryService.save(purchaseHistory);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        purchaseHistoryService.delete(id);
    }

    // ===== カスタム検索機能 =====

    @GetMapping("/item/{itemId}")
    public List<PurchaseHistory> getByItemId(@PathVariable Long itemId) {
        return purchaseHistoryService.findByItemId(itemId);
    }

    @GetMapping("/item/{itemId}/expiry-not-null")
    public List<PurchaseHistory> getByItemIdAndExpiryDateIsNotNull(@PathVariable Long itemId) {
        return purchaseHistoryService.findByItemIdAndExpiryDateIsNotNull(itemId);
    }

    @GetMapping("/period")
    public List<PurchaseHistory> getByPurchasedAtBetween(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return purchaseHistoryService.findByPurchasedAtBetween(start, end);
    }

    @GetMapping("/expired")
    public List<PurchaseHistory> getByExpiryDateBefore(@RequestParam LocalDate date) {
        return purchaseHistoryService.findByExpiryDateBefore(date);
    }
}
