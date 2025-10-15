// PurchaseHistoryRepository.java
package com.example.inventory.repository;

import com.example.inventory.entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
  // 商品ごとの購入履歴取得
  List<PurchaseHistory> findByItemIdOrderByPurchasedAtDesc(Long itemId);

  // 賞味期限付き商品の購入履歴
  List<PurchaseHistory> findByItemIdAndExpiryDateIsNotNullOrderByExpiryDateAsc(Long itemId);

  // 期間別購入履歴（統計機能で重要）
  List<PurchaseHistory> findByPurchasedAtBetween(LocalDateTime start, LocalDateTime end);

// 期限切れ近い購入品
  List<PurchaseHistory> findByExpiryDateBefore(LocalDate date);
}
