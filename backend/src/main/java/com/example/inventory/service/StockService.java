package com.example.inventory.service;

import com.example.inventory.entity.Stock;
import com.example.inventory.repository.StockRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Optional<Stock> findById(Long id) {
        return stockRepository.findById(id);
    }

    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    public void delete(Long id) {
        stockRepository.deleteById(id);
    }

    // 基本的な検索メソッド
    public List<Stock> findByItemId(Long itemId) {
        return stockRepository.findByItemId(itemId);
    }

    // カスタムメソッド（Repositoryの順番に合わせて整理）

    // 商品ごとの在庫を取得（期限順ソート版）
    public List<Stock> findByItemIdOrderByExpiryDateAsc(Long itemId) {
        return stockRepository.findByItemIdOrderByExpiryDateAsc(itemId);
    }

    // 期限が迫っている在庫
    public List<Stock> findByItemIdAndExpiryDateBefore(Long itemId, LocalDate date) {
        return stockRepository.findByItemIdAndExpiryDateBefore(itemId, date);
    }

    // 在庫数0のものを除外して取得
    public List<Stock> findByItemIdAndQuantityGreaterThan(Long itemId, int quantity) {
        return stockRepository.findByItemIdAndQuantityGreaterThan(itemId, quantity);
    }

    // 在庫数が閾値以下のもの
    public List<Stock> findByQuantityLessThanEqual(int quantity) {
        return stockRepository.findByQuantityLessThanEqual(quantity);
    }

    // 期限なし商品の在庫
    public List<Stock> findByExpiryDateIsNull() {
        return stockRepository.findByExpiryDateIsNull();
    }

    // 在庫合計計算（最重要！）
    public Integer getTotalQuantityByItemId(Long itemId) {
        return stockRepository.getTotalQuantityByItemId(itemId);
    }

    // ===== ビジネスロジック =====

    // 1. 在庫アラート機能
    public List<Stock> getLowStockAlerts() {
        // 全在庫をチェックして、閾値以下の商品の在庫を取得
        
        // ① stockテーブルから全レコードを取得し、List<Stock> として返す
        // findAll() で取得したリストを stream() によってストリーム処理に変換
        // → これで「1件ずつ順に処理して絞り込み・変換・集計」などができるようになる
        return findAll().stream()
        // ② quantity が 0 より大きいレコードのみ残す
        // → 在庫が 0 のもの（在庫切れ）は対象外にするため
                .filter(stock -> stock.getQuantity() > 0)
                // ③ 各 stock の item_id ごとの在庫合計(totalQuantity)を取得
        // getTotalQuantityByItemId() は同じ商品IDをもつ在庫の数量を合計して返すメソッド
                .filter(stock -> {
                    Integer totalQuantity = getTotalQuantityByItemId(stock.getItem().getId());

                    // ④ totalQuantity が null でなく、かつその値が閾値以下のものだけを残す
            // → 閾値を下回った商品だけが「在庫アラート対象」になる
                    return totalQuantity != null && totalQuantity <= stock.getItem().getThreshold();
                })
                // ⑤ フィルタ後の結果を List<Stock> にまとめて返す
                .toList();
    }

    public List<Stock> getLowStockAlertsByItemId(Long itemId) {
        // 特定商品の在庫アラート
        List<Stock> stocks = findByItemId(itemId);
        return stocks.stream()
                .filter(stock -> stock.getQuantity() > 0)
                .filter(stock -> {
                    // Itemのthresholdと比較
                    Integer totalQuantity = getTotalQuantityByItemId(itemId);
                    return totalQuantity != null && totalQuantity <= stock.getItem().getThreshold();
                })
                .toList();
    }

    // 2. 期限切れチェック機能
    public List<Stock> getExpiringStocks(int daysAhead) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        return findAll().stream()
                .filter(stock -> stock.getExpiryDate() != null)
                .filter(stock -> !stock.getExpiryDate().isAfter(targetDate))
                .filter(stock -> stock.getQuantity() > 0)
                .toList();
    }

    public List<Stock> getExpiredStocks() {
        LocalDate today = LocalDate.now();
        return findAll().stream()
                .filter(stock -> stock.getExpiryDate() != null)
                .filter(stock -> stock.getExpiryDate().isBefore(today))
                .filter(stock -> stock.getQuantity() > 0)
                .toList();
    }

    // 3. 在庫状況判定（UIでの色分け表示用）
    public enum StockStatus {
        NORMAL,      // 正常
        LOW_STOCK,   // 在庫少
        EXPIRING,    // 期限近（3日以内）
        EXPIRED      // 期限切れ
    }

    public StockStatus getStockStatus(Stock stock) {
        if (stock.getExpiryDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate expiryDate = stock.getExpiryDate();
            
            if (expiryDate.isBefore(today)) {
                return StockStatus.EXPIRED;
            } else if (expiryDate.isBefore(today.plusDays(3))) {
                return StockStatus.EXPIRING;
            }
        }
        
        // 在庫数のチェック
        Integer totalQuantity = getTotalQuantityByItemId(stock.getItem().getId());
        if (totalQuantity != null && totalQuantity <= stock.getItem().getThreshold()) {
            return StockStatus.LOW_STOCK;
        }
        
        return StockStatus.NORMAL;
    }
}
