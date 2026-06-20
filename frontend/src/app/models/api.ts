export interface ApiResponse<T> {
  status: string;
  data: T;
  error: string | null;
}

export interface AutoPart {
  sku: string;
  name: string;
  description: string;
  category: string;
  b2bPrice: number;
  inventoryLevel: number;
  imageUrl: string | null;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface LineItem {
  sku: string;
  partName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface SalesOrder {
  orderNumber: string;
  accountNumber: string;
  accountName: string;
  status: string;
  totalAmount: number;
  orderDate: string;
  items: LineItem[];
}

export interface B2BAccount {
  id: number;
  accountNumber: string;
  companyName: string;
  contactName: string;
  contactEmail: string;
  contactPhone: string;
  creditLimit: number;
  currentBalance: number;
  tier: string;
}
