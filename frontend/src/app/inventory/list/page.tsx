"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { apiClient } from "../../../lib/api";
import { Category, Item, Stock, StockStatus } from "../../../types/inventory";

export default function InventoryListPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [items, setItems] = useState<Item[]>([]);
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // データ取得
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesData, itemsData, stocksData] = await Promise.all([
          apiClient.categories.getAll(),
          apiClient.items.getAll(),
          apiClient.stocks.getAll(),
        ]);

        setCategories(categoriesData);
        setItems(itemsData);
        setStocks(stocksData);
      } catch (err) {
        setError("データの取得に失敗しました");
        console.error("Error fetching data:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // 在庫状況の判定
  const getStockStatus = (item: Item): StockStatus => {
    const itemStocks = stocks.filter((stock) => stock.item.id === item.id);
    const totalQuantity = itemStocks.reduce(
      (sum, stock) => sum + stock.quantity,
      0
    );

    // 期限切れチェック
    const today = new Date();
    const hasExpired = itemStocks.some(
      (stock) => stock.expiryDate && new Date(stock.expiryDate) < today
    );
    const hasExpiring = itemStocks.some((stock) => {
      if (!stock.expiryDate) return false;
      const expiryDate = new Date(stock.expiryDate);
      const threeDaysFromNow = new Date(
        today.getTime() + 3 * 24 * 60 * 60 * 1000
      );
      return expiryDate <= threeDaysFromNow && expiryDate >= today;
    });

    if (hasExpired) return StockStatus.EXPIRED;
    if (hasExpiring) return StockStatus.EXPIRING;
    if (totalQuantity <= item.threshold) return StockStatus.LOW_STOCK;
    return StockStatus.NORMAL;
  };

  // 在庫状況の色分け
  const getStatusColor = (status: StockStatus): string => {
    switch (status) {
      case StockStatus.EXPIRED:
        return "bg-red-100 text-red-800 border-red-200";
      case StockStatus.EXPIRING:
        return "bg-yellow-100 text-yellow-800 border-yellow-200";
      case StockStatus.LOW_STOCK:
        return "bg-orange-100 text-orange-800 border-orange-200";
      case StockStatus.NORMAL:
        return "bg-green-100 text-green-800 border-green-200";
      default:
        return "bg-gray-100 text-gray-800 border-gray-200";
    }
  };

  // 在庫状況の表示名
  const getStatusText = (status: StockStatus): string => {
    switch (status) {
      case StockStatus.EXPIRED:
        return "期限切れ";
      case StockStatus.EXPIRING:
        return "期限近";
      case StockStatus.LOW_STOCK:
        return "在庫少";
      case StockStatus.NORMAL:
        return "正常";
      default:
        return "不明";
    }
  };

  // 在庫数の計算
  const getTotalQuantity = (item: Item): number => {
    return stocks
      .filter((stock) => stock.item.id === item.id)
      .reduce((sum, stock) => sum + stock.quantity, 0);
  };

  // フィルタリングされたアイテム
  const filteredItems = selectedCategory
    ? items.filter((item) => item.category.id === selectedCategory)
    : items;

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">データを読み込み中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-red-600 mb-4">
            <svg
              className="w-12 h-12 mx-auto"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
          </div>
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            再試行
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* ヘッダー */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div className="flex items-center">
              <Link href="/" className="text-blue-600 hover:text-blue-800 mr-4">
                <svg
                  className="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M10 19l-7-7m0 0l7-7m-7 7h18"
                  />
                </svg>
              </Link>
              <h1 className="text-3xl font-bold text-gray-900">在庫一覧</h1>
            </div>
            <Link
              href="/inventory/add"
              className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center"
            >
              <svg
                className="w-5 h-5 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                />
              </svg>
              在庫を追加
            </Link>
          </div>
        </div>
      </header>

      {/* メインコンテンツ */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* カテゴリフィルター */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            カテゴリで絞り込み
          </h2>
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => setSelectedCategory(null)}
              className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                selectedCategory === null
                  ? "bg-blue-600 text-white"
                  : "bg-gray-100 text-gray-700 hover:bg-gray-200"
              }`}
            >
              すべて
            </button>
            {categories.map((category) => (
              <button
                key={category.id}
                onClick={() => setSelectedCategory(category.id)}
                className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                  selectedCategory === category.id
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                {category.name}
              </button>
            ))}
          </div>
        </div>

        {/* 在庫一覧 */}
        <div className="bg-white rounded-lg shadow-sm">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">
              在庫一覧{" "}
              {selectedCategory &&
                `(${categories.find((c) => c.id === selectedCategory)?.name})`}
            </h2>
          </div>

          {filteredItems.length === 0 ? (
            <div className="text-center py-12">
              <svg
                className="w-12 h-12 text-gray-400 mx-auto mb-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
                />
              </svg>
              <p className="text-gray-500">在庫がありません</p>
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              {filteredItems.map((item) => {
                const status = getStockStatus(item);
                const totalQuantity = getTotalQuantity(item);

                return (
                  <div
                    key={item.id}
                    className="p-6 hover:bg-gray-50 transition-colors"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center mb-2">
                          <h3 className="text-lg font-medium text-gray-900 mr-3">
                            {item.name}
                          </h3>
                          <span
                            className={`px-2 py-1 text-xs font-medium rounded-full border ${getStatusColor(
                              status
                            )}`}
                          >
                            {getStatusText(status)}
                          </span>
                        </div>

                        <div className="text-sm text-gray-600 space-y-1">
                          <p>カテゴリ: {item.category.name}</p>
                          {item.store && <p>購入店: {item.store.name}</p>}
                          <p>
                            在庫数: {totalQuantity}個 (閾値: {item.threshold}個)
                          </p>
                          {item.hasExpiry && (
                            <p>
                              期限:{" "}
                              {(() => {
                                const itemStocks = stocks.filter(
                                  (stock) => stock.item.id === item.id
                                );
                                const earliestExpiry = itemStocks
                                  .filter((stock) => stock.expiryDate)
                                  .sort(
                                    (a, b) =>
                                      new Date(a.expiryDate!).getTime() -
                                      new Date(b.expiryDate!).getTime()
                                  )[0];
                                return earliestExpiry?.expiryDate
                                  ? new Date(
                                      earliestExpiry.expiryDate
                                    ).toLocaleDateString("ja-JP")
                                  : "なし";
                              })()}
                            </p>
                          )}
                        </div>
                      </div>

                      <div className="flex items-center space-x-2">
                        <Link
                          href={`/inventory/edit/${item.id}`}
                          className="text-blue-600 hover:text-blue-800 p-2 rounded-lg hover:bg-blue-50"
                        >
                          <svg
                            className="w-5 h-5"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                            />
                          </svg>
                        </Link>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
