"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import Link from "next/link";
import { apiClient } from "../../../../lib/api";
import { Category, Store, Item, Stock } from "../../../../types/inventory";

export default function InventoryEditPage() {
  const router = useRouter();
  const params = useParams();
  const itemId = params.id as string;

  const [categories, setCategories] = useState<Category[]>([]);
  const [stores, setStores] = useState<Store[]>([]);
  const [item, setItem] = useState<Item | null>(null);
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // フォームの状態
  const [formData, setFormData] = useState({
    name: "",
    note: "",
    categoryId: "",
    storeId: "",
    threshold: 1,
    hasExpiry: false,
  });

  // 在庫の状態
  const [stockData, setStockData] = useState<{
    id: number | null;
    quantity: number;
    expiryDate: string;
  }>({
    id: null,
    quantity: 0,
    expiryDate: "",
  });

  // データ取得
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesData, storesData, itemData, stocksData] =
          await Promise.all([
            apiClient.categories.getAll(),
            apiClient.stores.getAll(),
            apiClient.items.getById(parseInt(itemId)),
            apiClient.stocks.getByItem(parseInt(itemId)),
          ]);

        setCategories(categoriesData);
        setStores(storesData);
        setItem(itemData);
        setStocks(stocksData);

        // フォームデータを設定
        setFormData({
          name: itemData.name,
          note: itemData.note || "",
          categoryId: itemData.category.id.toString(),
          storeId: itemData.store?.id?.toString() || "",
          threshold: itemData.threshold,
          hasExpiry: itemData.hasExpiry,
        });

        // 最初の在庫データを設定
        if (stocksData.length > 0) {
          const firstStock = stocksData[0];
          setStockData({
            id: firstStock.id,
            quantity: firstStock.quantity,
            expiryDate: firstStock.expiryDate || "",
          });
        }
      } catch (err) {
        setError("データの取得に失敗しました");
        console.error("Error fetching data:", err);
      } finally {
        setLoading(false);
      }
    };

    if (itemId) {
      fetchData();
    }
  }, [itemId]);

  // フォーム入力の処理
  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value, type } = e.target;

    if (type === "checkbox") {
      const checked = (e.target as HTMLInputElement).checked;
      setFormData((prev) => ({
        ...prev,
        [name]: checked,
      }));
    } else if (type === "number") {
      setFormData((prev) => ({
        ...prev,
        [name]: parseInt(value) || 0,
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  // 在庫データの処理
  const handleStockChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type } = e.target;
    setStockData((prev) => ({
      ...prev,
      [name]: type === "number" ? parseInt(value) || 0 : value,
    }));
  };

  // フォーム送信の処理
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.name.trim() || !formData.categoryId) {
      setError("品名とカテゴリは必須項目です");
      return;
    }

    if (formData.hasExpiry && !stockData.expiryDate) {
      setError("期限ありを選択した場合は期限日を入力してください");
      return;
    }

    try {
      setSubmitting(true);
      setError(null);

      // 1. アイテムを更新
      const selectedCategory = categories.find(
        (c) => c.id === parseInt(formData.categoryId)
      );
      const selectedStore = formData.storeId
        ? stores.find((s) => s.id === parseInt(formData.storeId))
        : undefined;

      if (!selectedCategory) {
        throw new Error("選択されたカテゴリが見つかりません");
      }

      const updatedItem: Partial<Item> = {
        name: formData.name.trim(),
        note: formData.note.trim() || undefined,
        category: selectedCategory,
        store: selectedStore,
        threshold: formData.threshold,
        hasExpiry: formData.hasExpiry,
      };

      await apiClient.items.update(parseInt(itemId), updatedItem);

      // 2. 在庫を更新
      if (stockData.id) {
        const updatedStock: Partial<Stock> = {
          quantity: stockData.quantity,
          expiryDate:
            formData.hasExpiry && stockData.expiryDate
              ? stockData.expiryDate
              : undefined,
        };

        await apiClient.stocks.update(stockData.id, updatedStock);
      }

      // 3. 成功時は一覧画面に遷移
      router.push("/inventory/list");
    } catch (err) {
      setError(err instanceof Error ? err.message : "更新に失敗しました");
      console.error("Error updating inventory:", err);
    } finally {
      setSubmitting(false);
    }
  };

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

  if (!item) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600">アイテムが見つかりません</p>
          <Link
            href="/inventory/list"
            className="text-blue-600 hover:text-blue-800 mt-4 inline-block"
          >
            一覧に戻る
          </Link>
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
              <Link
                href="/inventory/list"
                className="text-blue-600 hover:text-blue-800 mr-4"
              >
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
              <h1 className="text-3xl font-bold text-gray-900">在庫を更新</h1>
            </div>
          </div>
        </div>
      </header>

      {/* メインコンテンツ */}
      <main className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-sm">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">
              在庫情報を更新
            </h2>
            <p className="text-sm text-gray-600 mt-1">
              アイテム情報と在庫数を編集してください
            </p>
          </div>

          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            {/* エラー表示 */}
            {error && (
              <div className="bg-red-50 border border-red-200 rounded-md p-4">
                <div className="flex">
                  <svg
                    className="w-5 h-5 text-red-400"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                      clipRule="evenodd"
                    />
                  </svg>
                  <div className="ml-3">
                    <p className="text-sm text-red-800">{error}</p>
                  </div>
                </div>
              </div>
            )}

            {/* 品名 */}
            <div>
              <label
                htmlFor="name"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                品名 <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>

            {/* メモ */}
            <div>
              <label
                htmlFor="note"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                メモ
              </label>
              <textarea
                id="note"
                name="note"
                value={formData.note}
                onChange={handleInputChange}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            {/* カテゴリ */}
            <div>
              <label
                htmlFor="categoryId"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                ジャンル <span className="text-red-500">*</span>
              </label>
              <select
                id="categoryId"
                name="categoryId"
                value={formData.categoryId}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
              >
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>

            {/* 購入店 */}
            <div>
              <label
                htmlFor="storeId"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                購入店
              </label>
              <select
                id="storeId"
                name="storeId"
                value={formData.storeId}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">購入店を選択してください（任意）</option>
                {stores.map((store) => (
                  <option key={store.id} value={store.id}>
                    {store.name}
                  </option>
                ))}
              </select>
            </div>

            {/* 在庫アラート閾値 */}
            <div>
              <label
                htmlFor="threshold"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                在庫アラート閾値
              </label>
              <input
                type="number"
                id="threshold"
                name="threshold"
                value={formData.threshold}
                onChange={handleInputChange}
                min="1"
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>

            {/* 期限設定 */}
            <div>
              <div className="flex items-center mb-3">
                <input
                  type="checkbox"
                  id="hasExpiry"
                  name="hasExpiry"
                  checked={formData.hasExpiry}
                  onChange={handleInputChange}
                  className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label
                  htmlFor="hasExpiry"
                  className="ml-2 block text-sm font-medium text-gray-700"
                >
                  期限あり
                </label>
              </div>
            </div>

            {/* 在庫情報 */}
            <div className="border-t pt-6">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                在庫情報
              </h3>

              {/* 在庫数 */}
              <div className="mb-4">
                <label
                  htmlFor="quantity"
                  className="block text-sm font-medium text-gray-700 mb-2"
                >
                  在庫数
                </label>
                <input
                  type="number"
                  id="quantity"
                  name="quantity"
                  value={stockData.quantity}
                  onChange={handleStockChange}
                  min="0"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>

              {/* 期限日 */}
              {formData.hasExpiry && (
                <div>
                  <label
                    htmlFor="expiryDate"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    使用期限 <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="date"
                    id="expiryDate"
                    name="expiryDate"
                    value={stockData.expiryDate}
                    onChange={handleStockChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                    required={formData.hasExpiry}
                  />
                </div>
              )}
            </div>

            {/* ボタン */}
            <div className="flex justify-end space-x-4 pt-6 border-t border-gray-200">
              <Link
                href="/inventory/list"
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                キャンセル
              </Link>
              <button
                type="submit"
                disabled={submitting}
                className="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {submitting ? "更新中..." : "更新"}
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}
