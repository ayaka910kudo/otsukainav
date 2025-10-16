package com.example.inventory.controller;

import com.example.inventory.entity.Stock;
import com.example.inventory.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<Stock> getAll() {
        return stockService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getById(@PathVariable Long id) {
        Optional<Stock> stock = stockService.findById(id);
        return stock.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Stock create(@RequestBody Stock stock) {
        return stockService.save(stock);
    }

    @PutMapping("/{id}")
    public Stock update(@PathVariable Long id, @RequestBody Stock stock) {
        stock.setId(id);
        return stockService.save(stock);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        stockService.delete(id);
    }

    // ===== ビジネスロジックAPI =====

    // 在庫アラート機能
    @GetMapping("/alerts/low-stock")
    public List<Stock> getLowStockAlerts() {
        return stockService.getLowStockAlerts();
    }

    @GetMapping("/alerts/low-stock/{itemId}")
    public List<Stock> getLowStockAlertsByItemId(@PathVariable Long itemId) {
        return stockService.getLowStockAlertsByItemId(itemId);
    }

    // 期限切れチェック機能
    @GetMapping("/alerts/expiring")
    public List<Stock> getExpiringStocks(@RequestParam(defaultValue = "3") int daysAhead) {
        return stockService.getExpiringStocks(daysAhead);
    }

    @GetMapping("/alerts/expired")
    public List<Stock> getExpiredStocks() {
        return stockService.getExpiredStocks();
    }

    // 在庫状況判定
    @GetMapping("/{id}/status")
    public ResponseEntity<StockService.StockStatus> getStockStatus(@PathVariable Long id) {
        Optional<Stock> stock = stockService.findById(id);
        if (stock.isPresent()) {
            StockService.StockStatus status = stockService.getStockStatus(stock.get());
            return ResponseEntity.ok(status);
        }
        return ResponseEntity.notFound().build();
    }

    // カスタム検索機能
    @GetMapping("/item/{itemId}")
    public List<Stock> getStocksByItemId(@PathVariable Long itemId) {
        return stockService.findByItemId(itemId);
    }

    @GetMapping("/item/{itemId}/expiry-ordered")
    public List<Stock> getStocksByItemIdOrderByExpiryDate(@PathVariable Long itemId) {
        return stockService.findByItemIdOrderByExpiryDateAsc(itemId);
    }

    @GetMapping("/item/{itemId}/total-quantity")
    public Integer getTotalQuantityByItemId(@PathVariable Long itemId) {
        return stockService.getTotalQuantityByItemId(itemId);
    }
}
