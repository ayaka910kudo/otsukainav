package com.example.inventory.service;

import com.example.inventory.entity.Item;
import com.example.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    // 基本的な検索メソッド
    public List<Item> findByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    public List<Item> findByStoreId(Long storeId) {
        return itemRepository.findByStoreId(storeId);
    }

    // カスタムメソッド

    public List<Item> findByStoreIsNull() {
        return itemRepository.findByStoreIsNull();
    }

    public List<Item> findByNameContainingIgnoreCase(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Item> findByHasExpiry(boolean hasExpiry) {
        return itemRepository.findByHasExpiry(hasExpiry);
    }

    public List<Item> findByThresholdLessThan(int threshold) {
        return itemRepository.findByThresholdLessThan(threshold);
    }

    // Store削除時の関連解除
    @Transactional
    public int updateStoreToNull(Long storeId) {
        return itemRepository.updateStoreToNull(storeId);
    }
}
