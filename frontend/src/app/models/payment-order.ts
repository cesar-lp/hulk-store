export interface PaymentOrder {
  id: number;
  productId: number;
  productName: string;
  total: number;
  productStockLeft: number;
}