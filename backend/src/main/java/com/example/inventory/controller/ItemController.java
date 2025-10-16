package com.example.inventory.controller;

import com.example.inventory.entity.Item;
import com.example.inventory.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // 全件取得
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.findAll();
    }

    // ID指定で取得
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.findById(id);
        return item.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // 新規作成
    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.save(item);
    }

    // 更新
    @PutMapping("/{id}")
    public Item updateItem(@PathVariable Long id, @RequestBody Item item) {
        item.setId(id); // PathVariableのIDを設定
        return itemService.save(item);
    }

    // 削除
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.delete(id);
    }

    // ===== ビジネスロジックAPI =====

    // 在庫アラート機能
    @GetMapping("/alerts/low-stock")
    public List<Item> getItemsWithLowStock() {
        return itemService.getItemsWithLowStock();
    }

    // カスタム検索機能
    @GetMapping("/category/{categoryId}")
    public List<Item> getItemsByCategoryId(@PathVariable Long categoryId) {
        return itemService.findByCategoryId(categoryId);
    }

    @GetMapping("/store/{storeId}")
    public List<Item> getItemsByStoreId(@PathVariable Long storeId) {
        return itemService.findByStoreId(storeId);
    }

    @GetMapping("/store/null")
    public List<Item> getItemsWithNullStore() {
        return itemService.findByStoreIsNull();
    }

    @GetMapping("/search")
    public List<Item> searchItemsByName(@RequestParam String name) {
        return itemService.findByNameContainingIgnoreCase(name);
    }

    @GetMapping("/expiry/{hasExpiry}")
    public List<Item> getItemsByExpiryType(@PathVariable boolean hasExpiry) {
        return itemService.findByHasExpiry(hasExpiry);
    }

    @GetMapping("/threshold/{threshold}")
    public List<Item> getItemsByThreshold(@PathVariable int threshold) {
        return itemService.findByThresholdLessThan(threshold);
    }
}
