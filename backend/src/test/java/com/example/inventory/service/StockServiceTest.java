package com.example.inventory.service;

import com.example.inventory.entity.Stock;
import com.example.inventory.repository.StockRepository;
import com.example.inventory.util.TestConstants;
import com.example.inventory.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    // ===== 在庫アラート機能のテスト =====

    @Test
    @DisplayName("在庫アラート_正常系_閾値以下の在庫を取得")  // テストの説明（日本語OK、テスト実行時に表示される）
    void getLowStockAlerts_正常系_閾値以下の在庫を取得() {  // メソッド名（日本語OK、内容がわかる名前にする）
        // ===== Given（テストデータの準備） =====
        // 1. テスト用のデータセットを作成
        TestDataFactory.CategoryItemStockSet lowStockSet = TestDataFactory.createLowStockSet();  // 型名 変数名 = メソッド呼び出し; の構文
        TestDataFactory.CategoryItemStockSet normalStockSet = TestDataFactory.createNormalStockSet();
        
        // 2. テスト用のリストを作成（Arrays.asList()は複数要素をListに変換、通常のadd()を複数回呼ぶのと同じ効果）
        List<Stock> allStocks = Arrays.asList(
                lowStockSet.stock,    // 在庫2、閾値3 → アラート対象（.stockはCategoryItemStockSet内のStockフィールドを参照）
                normalStockSet.stock  // 在庫5、閾値2 → アラート対象外
        );

        // 3. Mockの動作を設定（when().thenReturn()で偽の動作を定義）
        when(stockRepository.findAll()).thenReturn(allStocks);  // findAll()が呼ばれたらallStocksを返す
        when(stockRepository.getTotalQuantityByItemId(lowStockSet.item.getId())).thenReturn(2);    // 閾値以下
        when(stockRepository.getTotalQuantityByItemId(normalStockSet.item.getId())).thenReturn(5);  // 閾値以上

        // ===== When（テスト対象メソッドの実行） =====
        // 4. 実際にテストしたいメソッドを実行
        List<Stock> lowStockAlerts = stockService.getLowStockAlerts();

        // ===== Then（結果の検証） =====
        // 5. 結果が期待通りかを確認（assertThat()で検証）
        assertThat(lowStockAlerts).hasSize(1);  // リストのサイズが1であることを確認
        assertThat(lowStockAlerts.get(0).getItem().getName()).isEqualTo(TestConstants.ITEM_NAME_SOY_SAUCE);  // 商品名が"醤油"であることを確認
        assertThat(lowStockAlerts.get(0).getQuantity()).isEqualTo(2);  // 在庫数が2であることを確認
    }

    @Test
    @DisplayName("在庫アラート_正常系_在庫0の商品は除外される")
    void getLowStockAlerts_正常系_在庫0の商品は除外される() {
        // Given
        TestDataFactory.CategoryItemStockSet lowStockSet = TestDataFactory.createLowStockSet();
        Stock zeroStock = TestDataFactory.createStock(
                lowStockSet.item, 
                TestConstants.QUANTITY_ZERO,  // 在庫0
                TestConstants.EXPIRING_LATER
        );
        
        List<Stock> allStocks = Arrays.asList(lowStockSet.stock, zeroStock);

        when(stockRepository.findAll()).thenReturn(allStocks);
        when(stockRepository.getTotalQuantityByItemId(lowStockSet.item.getId())).thenReturn(2);

        // When
        List<Stock> lowStockAlerts = stockService.getLowStockAlerts();

        // Then
        assertThat(lowStockAlerts).hasSize(1);  // 在庫0の商品は除外される
        assertThat(lowStockAlerts.get(0).getQuantity()).isGreaterThan(0);
    }

    @Test
    @DisplayName("在庫アラート_正常系_アラート対象なし")
    void getLowStockAlerts_正常系_アラート対象なし() {
        // Given
        TestDataFactory.CategoryItemStockSet normalStockSet = TestDataFactory.createNormalStockSet();
        List<Stock> allStocks = Arrays.asList(normalStockSet.stock);

        when(stockRepository.findAll()).thenReturn(allStocks);
        when(stockRepository.getTotalQuantityByItemId(normalStockSet.item.getId())).thenReturn(5);  // 閾値以上

        // When
        List<Stock> lowStockAlerts = stockService.getLowStockAlerts();

        // Then
        assertThat(lowStockAlerts).isEmpty();
    }

    // ===== 期限切れチェック機能のテスト =====

    @Test
    @DisplayName("期限切れチェック_正常系_3日以内に期限切れの在庫を取得")
    void getExpiringStocks_正常系_3日以内に期限切れの在庫を取得() {
        // Given
        TestDataFactory.CategoryItemStockSet expiringSet = TestDataFactory.createExpiringStockSet();
        TestDataFactory.CategoryItemStockSet normalSet = TestDataFactory.createNormalStockSet();
        
        List<Stock> allStocks = Arrays.asList(
                expiringSet.stock,  // 2日後に期限切れ → 対象
                normalSet.stock     // 30日後に期限切れ → 対象外
        );

        when(stockRepository.findAll()).thenReturn(allStocks);

        // When
        List<Stock> expiringStocks = stockService.getExpiringStocks(3);

        // Then
        assertThat(expiringStocks).hasSize(1);
        assertThat(expiringStocks.get(0).getItem().getName()).isEqualTo(TestConstants.ITEM_NAME_FROZEN_GYOZA);
        assertThat(expiringStocks.get(0).getExpiryDate()).isEqualTo(TestConstants.EXPIRING_SOON);
    }

    @Test
    @DisplayName("期限切れチェック_正常系_在庫0の商品は除外される")
    void getExpiringStocks_正常系_在庫0の商品は除外される() {
        // Given
        TestDataFactory.CategoryItemStockSet expiringSet = TestDataFactory.createExpiringStockSet();
        Stock zeroStock = TestDataFactory.createStock(
                expiringSet.item, 
                TestConstants.QUANTITY_ZERO,  // 在庫0
                TestConstants.EXPIRING_SOON
        );
        
        List<Stock> allStocks = Arrays.asList(expiringSet.stock, zeroStock);

        when(stockRepository.findAll()).thenReturn(allStocks);

        // When
        List<Stock> expiringStocks = stockService.getExpiringStocks(3);

        // Then
        assertThat(expiringStocks).hasSize(1);  // 在庫0の商品は除外される
        assertThat(expiringStocks.get(0).getQuantity()).isGreaterThan(0);
    }

    @Test
    @DisplayName("期限切れチェック_正常系_期限なし商品は除外される")
    void getExpiringStocks_正常系_期限なし商品は除外される() {
        // Given
        TestDataFactory.CategoryItemStockSet noExpirySet = TestDataFactory.createNoExpiryStockSet();
        TestDataFactory.CategoryItemStockSet expiringSet = TestDataFactory.createExpiringStockSet();
        
        List<Stock> allStocks = Arrays.asList(noExpirySet.stock, expiringSet.stock);

        when(stockRepository.findAll()).thenReturn(allStocks);

        // When
        List<Stock> expiringStocks = stockService.getExpiringStocks(3);

        // Then
        assertThat(expiringStocks).hasSize(1);  // 期限なし商品は除外される
        assertThat(expiringStocks.get(0).getExpiryDate()).isNotNull();
    }

    // ===== 在庫状況判定機能のテスト =====

    @Test
    @DisplayName("在庫状況判定_正常系_期限切れの場合")
    void getStockStatus_正常系_期限切れの場合() {
        // Given
        TestDataFactory.CategoryItemStockSet expiredSet = TestDataFactory.createExpiredStockSet();
        Stock expiredStock = expiredSet.stock;

        // 期限切れの場合は在庫数チェックは不要なので、Mock設定なし

        // When
        StockService.StockStatus status = stockService.getStockStatus(expiredStock);

        // Then
        assertThat(status).isEqualTo(StockService.StockStatus.EXPIRED);
    }

    @Test
    @DisplayName("在庫状況判定_正常系_期限切れ近い場合")
    void getStockStatus_正常系_期限切れ近い場合() {
        // Given
        TestDataFactory.CategoryItemStockSet expiringSet = TestDataFactory.createExpiringStockSet();
        Stock expiringStock = expiringSet.stock;

        // 期限切れ近い場合は在庫数チェックは不要なので、Mock設定なし

        // When
        StockService.StockStatus status = stockService.getStockStatus(expiringStock);

        // Then
        assertThat(status).isEqualTo(StockService.StockStatus.EXPIRING);
    }

    @Test
    @DisplayName("在庫状況判定_正常系_在庫少の場合")
    void getStockStatus_正常系_在庫少の場合() {
        // Given
        TestDataFactory.CategoryItemStockSet lowStockSet = TestDataFactory.createLowStockSet();
        Stock lowStock = lowStockSet.stock;

        when(stockRepository.getTotalQuantityByItemId(lowStock.getItem().getId())).thenReturn(2);  // 閾値以下

        // When
        StockService.StockStatus status = stockService.getStockStatus(lowStock);

        // Then
        assertThat(status).isEqualTo(StockService.StockStatus.LOW_STOCK);
    }

    @Test
    @DisplayName("在庫状況判定_正常系_正常な場合")
    void getStockStatus_正常系_正常な場合() {
        // Given
        TestDataFactory.CategoryItemStockSet normalSet = TestDataFactory.createNormalStockSet();
        Stock normalStock = normalSet.stock;

        when(stockRepository.getTotalQuantityByItemId(normalStock.getItem().getId())).thenReturn(5);  // 閾値以上

        // When
        StockService.StockStatus status = stockService.getStockStatus(normalStock);

        // Then
        assertThat(status).isEqualTo(StockService.StockStatus.NORMAL);
    }
}