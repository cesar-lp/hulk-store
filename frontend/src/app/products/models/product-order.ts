export interface ProductOrder {
  id: number;
  createdAt: string;
  productOrderLines: ProductOrderLine[];
  total: number;
}

export interface ProductOrderLine {
  id: number;
  productName: string;
  productPrice: number;
  quantity: number;
  total: number;
}

export interface ProductOrderRequest {
  orderLines: OrderLineRequest[];
}

export interface OrderLineRequest {
  productId: number;
  quantity: number;
}
