// APIクライアント - バックエンドとの通信を管理

import {
  Category,
  Store,
  Item,
  Stock,
  PurchaseHistory,
  StockStatus,
  ApiError,
} from "@/types/inventory";

// APIのベースURL
const API_BASE_URL = "http://localhost:8080";

// エラーハンドリング用のカスタムエラークラス
export class ApiClientError extends Error {
  constructor(
    message: string,
    public status: number,
    public response?: Response
  ) {
    super(message);
    this.name = "ApiClientError";
  }
}

// 基本的なHTTPリクエスト関数
async function request<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;

  const config: RequestInit = {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  };

  try {
    const response = await fetch(url, config);

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new ApiClientError(
        errorData.message || `HTTP Error: ${response.status}`,
        response.status,
        response
      );
    }

    // 204 No Content の場合は空のオブジェクトを返す
    if (response.status === 204) {
      return {} as T;
    }

    return await response.json();
  } catch (error) {
    if (error instanceof ApiClientError) {
      throw error;
    }
    throw new ApiClientError(
      `Network Error: ${
        error instanceof Error ? error.message : "Unknown error"
      }`,
      0
    );
  }
}

// カテゴリ関連のAPI
export const categoryApi = {
  // 全カテゴリ取得
  getAll: (): Promise<Category[]> => request<Category[]>("/categories"),

  // IDでカテゴリ取得
  getById: (id: number): Promise<Category> =>
    request<Category>(`/categories/${id}`),

  // カテゴリ作成
  create: (
    category: Omit<Category, "id" | "createdAt" | "updatedAt">
  ): Promise<Category> =>
    request<Category>("/categories", {
      method: "POST",
      body: JSON.stringify(category),
    }),

  // カテゴリ更新
  update: (id: number, category: Partial<Category>): Promise<Category> =>
    request<Category>(`/categories/${id}`, {
      method: "PUT",
      body: JSON.stringify(category),
    }),

  // カテゴリ削除
  delete: (id: number): Promise<void> =>
    request<void>(`/categories/${id}`, {
      method: "DELETE",
    }),

  // 名前で検索
  searchByName: (keyword: string): Promise<Category[]> =>
    request<Category[]>(
      `/categories/search?keyword=${encodeURIComponent(keyword)}`
    ),
};

// ストア関連のAPI
export const storeApi = {
  // 全ストア取得
  getAll: (): Promise<Store[]> => request<Store[]>("/stores"),

  // IDでストア取得
  getById: (id: number): Promise<Store> => request<Store>(`/stores/${id}`),

  // ストア作成
  create: (
    store: Omit<Store, "id" | "createdAt" | "updatedAt">
  ): Promise<Store> =>
    request<Store>("/stores", {
      method: "POST",
      body: JSON.stringify(store),
    }),

  // ストア更新
  update: (id: number, store: Partial<Store>): Promise<Store> =>
    request<Store>(`/stores/${id}`, {
      method: "PUT",
      body: JSON.stringify(store),
    }),

  // ストア削除
  delete: (id: number): Promise<void> =>
    request<void>(`/stores/${id}`, {
      method: "DELETE",
    }),

  // 名前で検索
  searchByName: (keyword: string): Promise<Store[]> =>
    request<Store[]>(`/stores/search?keyword=${encodeURIComponent(keyword)}`),
};

// アイテム関連のAPI
export const itemApi = {
  // 全アイテム取得
  getAll: (): Promise<Item[]> => request<Item[]>("/items"),

  // IDでアイテム取得
  getById: (id: number): Promise<Item> => request<Item>(`/items/${id}`),

  // アイテム作成
  create: (item: Omit<Item, "id" | "createdAt" | "updatedAt">): Promise<Item> =>
    request<Item>("/items", {
      method: "POST",
      body: JSON.stringify(item),
    }),

  // アイテム更新
  update: (id: number, item: Partial<Item>): Promise<Item> =>
    request<Item>(`/items/${id}`, {
      method: "PUT",
      body: JSON.stringify(item),
    }),

  // アイテム削除
  delete: (id: number): Promise<void> =>
    request<void>(`/items/${id}`, {
      method: "DELETE",
    }),

  // カテゴリ別アイテム取得
  getByCategory: (categoryId: number): Promise<Item[]> =>
    request<Item[]>(`/items/category/${categoryId}`),

  // ストア別アイテム取得
  getByStore: (storeId: number): Promise<Item[]> =>
    request<Item[]>(`/items/store/${storeId}`),

  // 在庫アラート対象アイテム取得
  getLowStockItems: (): Promise<Item[]> =>
    request<Item[]>("/items/alerts/low-stock"),

  // 名前で検索
  searchByName: (name: string): Promise<Item[]> =>
    request<Item[]>(`/items/search?name=${encodeURIComponent(name)}`),
};

// 在庫関連のAPI
export const stockApi = {
  // 全在庫取得
  getAll: (): Promise<Stock[]> => request<Stock[]>("/stocks"),

  // IDで在庫取得
  getById: (id: number): Promise<Stock> => request<Stock>(`/stocks/${id}`),

  // 在庫作成
  create: (
    stock: Omit<Stock, "id" | "createdAt" | "updatedAt">
  ): Promise<Stock> =>
    request<Stock>("/stocks", {
      method: "POST",
      body: JSON.stringify(stock),
    }),

  // 在庫更新
  update: (id: number, stock: Partial<Stock>): Promise<Stock> =>
    request<Stock>(`/stocks/${id}`, {
      method: "PUT",
      body: JSON.stringify(stock),
    }),

  // 在庫削除
  delete: (id: number): Promise<void> =>
    request<void>(`/stocks/${id}`, {
      method: "DELETE",
    }),

  // アイテム別在庫取得
  getByItem: (itemId: number): Promise<Stock[]> =>
    request<Stock[]>(`/stocks/item/${itemId}`),

  // 在庫アラート取得
  getLowStockAlerts: (): Promise<Stock[]> =>
    request<Stock[]>("/stocks/alerts/low-stock"),

  // 期限切れ近い在庫取得
  getExpiringStocks: (daysAhead: number = 3): Promise<Stock[]> =>
    request<Stock[]>(`/stocks/alerts/expiring?daysAhead=${daysAhead}`),

  // 期限切れ在庫取得
  getExpiredStocks: (): Promise<Stock[]> =>
    request<Stock[]>("/stocks/alerts/expired"),

  // 在庫状況判定
  getStockStatus: (id: number): Promise<StockStatus> =>
    request<StockStatus>(`/stocks/${id}/status`),

  // アイテムの在庫合計取得
  getTotalQuantity: (itemId: number): Promise<number> =>
    request<number>(`/stocks/item/${itemId}/total-quantity`),
};

// 購入履歴関連のAPI
export const purchaseHistoryApi = {
  // 全購入履歴取得
  getAll: (): Promise<PurchaseHistory[]> =>
    request<PurchaseHistory[]>("/purchaseHistories"),

  // IDで購入履歴取得
  getById: (id: number): Promise<PurchaseHistory> =>
    request<PurchaseHistory>(`/purchaseHistories/${id}`),

  // 購入履歴作成
  create: (
    purchaseHistory: Omit<PurchaseHistory, "id" | "createdAt" | "updatedAt">
  ): Promise<PurchaseHistory> =>
    request<PurchaseHistory>("/purchaseHistories", {
      method: "POST",
      body: JSON.stringify(purchaseHistory),
    }),

  // 購入履歴更新
  update: (
    id: number,
    purchaseHistory: Partial<PurchaseHistory>
  ): Promise<PurchaseHistory> =>
    request<PurchaseHistory>(`/purchaseHistories/${id}`, {
      method: "PUT",
      body: JSON.stringify(purchaseHistory),
    }),

  // 購入履歴削除
  delete: (id: number): Promise<void> =>
    request<void>(`/purchaseHistories/${id}`, {
      method: "DELETE",
    }),

  // アイテム別購入履歴取得
  getByItem: (itemId: number): Promise<PurchaseHistory[]> =>
    request<PurchaseHistory[]>(`/purchaseHistories/item/${itemId}`),

  // 期間別購入履歴取得
  getByPeriod: (start: string, end: string): Promise<PurchaseHistory[]> =>
    request<PurchaseHistory[]>(
      `/purchaseHistories/period?start=${start}&end=${end}`
    ),
};

// エクスポート用のAPIクライアント
export const apiClient = {
  categories: categoryApi,
  stores: storeApi,
  items: itemApi,
  stocks: stockApi,
  purchaseHistories: purchaseHistoryApi,
};
