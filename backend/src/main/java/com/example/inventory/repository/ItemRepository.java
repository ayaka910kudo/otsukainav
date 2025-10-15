
package com.example.inventory.repository;

import com.example.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  // 関連エンティティ検索（category_id, store_id は Item -> Category/Store のプロパティ経由で探索）
  List<Item> findByCategoryId(Long categoryId);
  List<Item> findByStoreId(Long storeId);
  List<Item> findByStoreIsNull();

 // 名前・条件検索
  List<Item> findByNameContainingIgnoreCase(String name);
  List<Item> findByHasExpiry(boolean hasExpiry);
  List<Item> findByThresholdLessThan(int threshold);

 // Store 削除用（関連を解除する一括更新）
  @Modifying
  @Query("UPDATE Item i SET i.store = null WHERE i.store.id = :storeId")
  int updateStoreToNull(@Param("storeId") Long storeId);

}