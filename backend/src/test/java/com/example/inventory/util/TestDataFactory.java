package com.example.inventory.util;

import com.example.inventory.entity.Category;
import com.example.inventory.entity.Item;
import com.example.inventory.entity.Stock;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * テスト用データを作成するファクトリークラス
 * StockServiceの主要ビジネスロジックテスト用に特化
 */
public class TestDataFactory {

    // ===== Category テストデータ =====
    
    /**
     * 基本的なカテゴリを作成
     */
    public static Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * ID付きのカテゴリを作成
     */
    public static Category createCategory(Long id, String name) {
        return Category.builder()
                .id(id)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    // ===== Item テストデータ =====
    
    /**
     * 基本的な商品を作成
     */
    public static Item createItem(String name, Category category, int threshold) {
        return Item.builder()
                .name(name)
                .category(category)
                .threshold(threshold)
                .hasExpiry(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 期限なし商品を作成
     */
    public static Item createItemWithoutExpiry(String name, Category category, int threshold) {
        return Item.builder()
                .name(name)
                .category(category)
                .threshold(threshold)
                .hasExpiry(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * ID付きの商品を作成
     */
    public static Item createItem(Long id, String name, Category category, int threshold, boolean hasExpiry) {
        return Item.builder()
                .id(id)
                .name(name)
                .category(category)
                .threshold(threshold)
                .hasExpiry(hasExpiry)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    // ===== Stock テストデータ =====
    
    /**
     * 基本的な在庫を作成
     */
    public static Stock createStock(Item item, int quantity, LocalDate expiryDate) {
        return Stock.builder()
                .item(item)
                .quantity(quantity)
                .expiryDate(expiryDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 期限なし在庫を作成
     */
    public static Stock createStockWithoutExpiry(Item item, int quantity) {
        return Stock.builder()
                .item(item)
                .quantity(quantity)
                .expiryDate(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * ID付きの在庫を作成
     */
    public static Stock createStock(Long id, Item item, int quantity, LocalDate expiryDate) {
        return Stock.builder()
                .id(id)
                .item(item)
                .quantity(quantity)
                .expiryDate(expiryDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    // ===== 複合データ作成メソッド（主要ビジネスロジック用） =====
    
    /**
     * カテゴリ + 商品 + 在庫のセットを作成
     */
    public static class CategoryItemStockSet {
        public final Category category;
        public final Item item;
        public final Stock stock;
        
        public CategoryItemStockSet(Category category, Item item, Stock stock) {
            this.category = category;
            this.item = item;
            this.stock = stock;
        }
    }
    
    /**
     * 在庫アラート用のテストデータセット（閾値以下の在庫）
     */
    public static CategoryItemStockSet createLowStockSet() {
        Category category = createCategory(TestConstants.CATEGORY_ID_1, TestConstants.CATEGORY_NAME_SEASONING);
        Item item = createItem(TestConstants.ITEM_ID_1, TestConstants.ITEM_NAME_SOY_SAUCE, category, TestConstants.THRESHOLD_MEDIUM, true);  // 閾値: 3
        Stock stock = createStock(TestConstants.STOCK_ID_1, item, TestConstants.QUANTITY_LOW, TestConstants.EXPIRING_LATER);  // 在庫: 2（閾値以下）
        return new CategoryItemStockSet(category, item, stock);
    }
    
    /**
     * 正常在庫用のテストデータセット（閾値以上の在庫）
     */
    public static CategoryItemStockSet createNormalStockSet() {
        Category category = createCategory(TestConstants.CATEGORY_ID_2, TestConstants.CATEGORY_NAME_SEASONING);
        Item item = createItem(TestConstants.ITEM_ID_2, TestConstants.ITEM_NAME_MISO, category, TestConstants.THRESHOLD_LOW, true);  // 閾値: 2
        Stock stock = createStock(TestConstants.STOCK_ID_2, item, TestConstants.QUANTITY_HIGH, TestConstants.EXPIRING_LATER);  // 在庫: 5（閾値以上）
        return new CategoryItemStockSet(category, item, stock);
    }
    
    /**
     * 期限切れ近い在庫用のテストデータセット
     */
    public static CategoryItemStockSet createExpiringStockSet() {
        Category category = createCategory(TestConstants.CATEGORY_ID_1, TestConstants.CATEGORY_NAME_FROZEN_FOOD);
        Item item = createItem(TestConstants.ITEM_ID_3, TestConstants.ITEM_NAME_FROZEN_GYOZA, category, TestConstants.THRESHOLD_LOW, true);
        Stock stock = createStock(TestConstants.STOCK_ID_1, item, TestConstants.QUANTITY_MEDIUM, TestConstants.EXPIRING_SOON);  // 2日後に期限切れ
        return new CategoryItemStockSet(category, item, stock);
    }
    
    /**
     * 期限切れ在庫用のテストデータセット
     */
    public static CategoryItemStockSet createExpiredStockSet() {
        Category category = createCategory(TestConstants.CATEGORY_ID_2, TestConstants.CATEGORY_NAME_FROZEN_FOOD);
        Item item = createItem(TestConstants.ITEM_ID_2, TestConstants.ITEM_NAME_FROZEN_PIZZA, category, TestConstants.THRESHOLD_LOW, true);
        Stock stock = createStock(TestConstants.STOCK_ID_2, item, TestConstants.QUANTITY_LOW, TestConstants.YESTERDAY);  // 昨日期限切れ
        return new CategoryItemStockSet(category, item, stock);
    }
    
    /**
     * 期限なし商品用のテストデータセット
     */
    public static CategoryItemStockSet createNoExpiryStockSet() {
        Category category = createCategory(TestConstants.CATEGORY_ID_1, TestConstants.CATEGORY_NAME_DAILY_GOODS);
        Item item = createItem(TestConstants.ITEM_ID_1, TestConstants.ITEM_NAME_TOOTHPASTE, category, TestConstants.THRESHOLD_LOW, false);
        Stock stock = createStock(TestConstants.STOCK_ID_1, item, TestConstants.QUANTITY_MEDIUM, null);  // 期限なし
        return new CategoryItemStockSet(category, item, stock);
    }
}