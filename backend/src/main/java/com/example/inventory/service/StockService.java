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
}
