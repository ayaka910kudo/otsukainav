// StockRepository.java
package com.example.inventory.repository;

import com.example.inventory.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

  // 商品ごとの在庫を取得（基本版）
  List<Stock> findByItemId(Long itemId);

  // 商品ごとの在庫を取得（期限順ソート版）
  List<Stock> findByItemIdOrderByExpiryDateAsc(Long itemId);

  // 期限が迫っている在庫
  List<Stock> findByItemIdAndExpiryDateBefore(Long itemId, LocalDate date);

  // 在庫数0のものを除外して取得
  List<Stock> findByItemIdAndQuantityGreaterThan(Long itemId, int quantity);

  // 在庫数が閾値以下のものを検索（アラート機能で必須）
List<Stock> findByQuantityLessThanEqual(int quantity);

// 期限なし商品の在庫
List<Stock> findByExpiryDateIsNull();

// 商品の現在在庫合計を計算（重要！）
@Query("SELECT SUM(s.quantity) FROM Stock s WHERE s.item.id = :itemId AND s.quantity > 0")
Integer getTotalQuantityByItemId(@Param("itemId") Long itemId);
}
