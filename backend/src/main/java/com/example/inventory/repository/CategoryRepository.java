// CategoryRepository.java
package com.example.inventory.repository;

import com.example.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  // 名前検索
  Optional<Category> findByName(String name);
  // 名前存在確認
  boolean existsByName(String name);
  // 名前部分一致検索
  List<Category> findByNameContainingIgnoreCase(String name);
}
