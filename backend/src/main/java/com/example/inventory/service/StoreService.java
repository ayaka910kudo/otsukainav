package com.example.inventory.service;

import com.example.inventory.entity.Store;
import com.example.inventory.repository.StoreRepository;
import com.example.inventory.service.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final ItemService itemService;

    public StoreService(StoreRepository storeRepository, ItemService itemService) {
        this.storeRepository = storeRepository;
        this.itemService = itemService;
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Optional<Store> findById(Long id) {
        return storeRepository.findById(id);
    }

    public Store save(Store store) {
        return storeRepository.save(store);
    }

    // 安全なStore削除（関連するItemのstore_idをNULLに設定してから削除）
    @Transactional
    public void delete(Long id) {
        // 1. 関連するItemのstore_idをNULLに設定
        int updatedItems = itemService.updateStoreToNull(id);
        
        // 2. Storeを削除
        storeRepository.deleteById(id);
    }

    // 基本的な検索メソッド
    public Optional<Store> findByName(String name) {
        return storeRepository.findByName(name);
    }

    // カスタムメソッド
    public boolean existsByName(String name) {
        return storeRepository.existsByName(name);
    }

    public List<Store> searchByName(String keyword) {
        return storeRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Store> findByLocationContainingIgnoreCase(String location) {
        return storeRepository.findByLocationContainingIgnoreCase(location);
    }
}
