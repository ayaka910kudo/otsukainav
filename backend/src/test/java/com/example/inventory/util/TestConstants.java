package com.example.inventory.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * テスト用の定数クラス
 * StockServiceの主要ビジネスロジックテスト用に特化
 */
public class TestConstants {

    // ===== テスト用ID =====
    public static final Long CATEGORY_ID_1 = 1L;
    public static final Long CATEGORY_ID_2 = 2L;
    
    public static final Long ITEM_ID_1 = 1L;
    public static final Long ITEM_ID_2 = 2L;
    public static final Long ITEM_ID_3 = 3L;
    
    public static final Long STOCK_ID_1 = 1L;
    public static final Long STOCK_ID_2 = 2L;
    
    // ===== テスト用名前 =====
    public static final String CATEGORY_NAME_SEASONING = "調味料";
    public static final String CATEGORY_NAME_DAILY_GOODS = "日用品";
    public static final String CATEGORY_NAME_FROZEN_FOOD = "冷凍食品";
    
    public static final String ITEM_NAME_SOY_SAUCE = "醤油";
    public static final String ITEM_NAME_MISO = "味噌";
    public static final String ITEM_NAME_TOOTHPASTE = "歯磨き粉";
    public static final String ITEM_NAME_FROZEN_GYOZA = "冷凍餃子";
    public static final String ITEM_NAME_FROZEN_PIZZA = "冷凍ピザ";
    
    // ===== テスト用数値（在庫アラート用） =====
    public static final int THRESHOLD_LOW = 2;      // 低い閾値
    public static final int THRESHOLD_MEDIUM = 3;   // 中程度の閾値
    public static final int THRESHOLD_HIGH = 5;     // 高い閾値
    
    public static final int QUANTITY_ZERO = 0;      // 在庫なし
    public static final int QUANTITY_LOW = 2;       // 少ない在庫（閾値以下）
    public static final int QUANTITY_MEDIUM = 3;    // 中程度の在庫
    public static final int QUANTITY_HIGH = 5;      // 多い在庫（閾値以上）
    
    // ===== テスト用日付（期限切れチェック用） =====
    public static final LocalDate TODAY = LocalDate.now();
    public static final LocalDate YESTERDAY = TODAY.minusDays(1);       // 昨日（期限切れ）
    public static final LocalDate TOMORROW = TODAY.plusDays(1);
    public static final LocalDate EXPIRING_SOON = TODAY.plusDays(2);    // 3日以内（期限切れ近い）
    public static final LocalDate EXPIRING_LATER = TODAY.plusDays(30);  // 30日後（まだ余裕）
    
    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final LocalDateTime ONE_HOUR_AGO = NOW.minusHours(1);
    public static final LocalDateTime ONE_DAY_AGO = NOW.minusDays(1);
}