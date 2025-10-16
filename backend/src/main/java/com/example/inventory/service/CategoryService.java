package com.example.inventory.service;

import com.example.inventory.entity.Category;
import com.example.inventory.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 一覧取得
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // ID検索
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    // 登録・更新
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    // 削除
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    // 基本的な検索メソッド
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    // カスタムメソッド

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public List<Category> searchByName(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }
}
