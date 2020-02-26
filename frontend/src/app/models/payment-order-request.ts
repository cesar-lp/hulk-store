export interface PaymentOrderRequest {
  orderLines: OrderLineRequest[];
}

export interface OrderLineRequest {
  productId: number;
  quantity: number;
}
