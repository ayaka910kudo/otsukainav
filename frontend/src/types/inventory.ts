// 在庫管理アプリの型定義

export interface Category {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
  items?: Item[];
}

export interface Store {
  id: number;
  name: string;
  location?: string;
  createdAt: string;
  updatedAt: string;
  items?: Item[];
  purchaseHistories?: PurchaseHistory[];
}

export interface Item {
  id: number;
  name: string;
  note?: string;
  category: Category;
  store?: Store;
  threshold: number;
  hasExpiry: boolean;
  createdAt: string;
  updatedAt: string;
  stocks?: Stock[];
  purchaseHistories?: PurchaseHistory[];
}

export interface Stock {
  id: number;
  item: Item;
  quantity: number;
  expiryDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PurchaseHistory {
  id: number;
  item: Item;
  quantity: number;
  totalPrice?: number;
  expiryDate?: string;
  purchasedAt: string;
  createdAt: string;
  updatedAt: string;
}

// 在庫状況の判定結果
export enum StockStatus {
  NORMAL = 'NORMAL',
  LOW_STOCK = 'LOW_STOCK',
  EXPIRING = 'EXPIRING',
  EXPIRED = 'EXPIRED'
}

// API レスポンスの型
export interface ApiResponse<T> {
  data: T;
  message?: string;
  status: number;
}

// エラーレスポンスの型
export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
  path: string;
}
