// StoreRepository.java
package com.example.inventory.repository;

import com.example.inventory.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
  // 名前存在確認(重複防止)
  boolean existsByName(String name);

  // 名前で検索（Store選択時に必要）
  Optional<Store> findByName(String name);

  // 場所で検索
  List<Store> findByLocationContainingIgnoreCase(String location);
}
