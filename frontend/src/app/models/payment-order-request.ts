export interface PaymentOrderRequest {
  productOrders: ProductOrderRequest[];
}

export interface ProductOrderRequest {
  productId: number;
  quantity: number;
}