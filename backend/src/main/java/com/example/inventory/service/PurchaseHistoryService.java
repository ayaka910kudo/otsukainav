package com.example.inventory.service;

import com.example.inventory.entity.PurchaseHistory;
import com.example.inventory.repository.PurchaseHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    public List<PurchaseHistory> findAll() {
        return purchaseHistoryRepository.findAll();
    }

    public Optional<PurchaseHistory> findById(Long id) {
        return purchaseHistoryRepository.findById(id);
    }

    public PurchaseHistory save(PurchaseHistory purchaseHistory) {
        return purchaseHistoryRepository.save(purchaseHistory);
    }

    public void delete(Long id) {
        purchaseHistoryRepository.deleteById(id);
    }

    // 基本的な検索メソッド
    public List<PurchaseHistory> findByItemId(Long itemId) {
        return purchaseHistoryRepository.findByItemIdOrderByPurchasedAtDesc(itemId);
    }

    // カスタムメソッド
    // 賞味期限付き商品の購入履歴
    public List<PurchaseHistory> findByItemIdAndExpiryDateIsNotNull(Long itemId) {
        return purchaseHistoryRepository.findByItemIdAndExpiryDateIsNotNullOrderByExpiryDateAsc(itemId);
    }

    // 期間別購入履歴
    public List<PurchaseHistory> findByPurchasedAtBetween(LocalDateTime start, LocalDateTime end) {
        return purchaseHistoryRepository.findByPurchasedAtBetween(start, end);
    }

    // 期限切れ近い購入品
    public List<PurchaseHistory> findByExpiryDateBefore(LocalDate date) {
        return purchaseHistoryRepository.findByExpiryDateBefore(date);
    }
}
