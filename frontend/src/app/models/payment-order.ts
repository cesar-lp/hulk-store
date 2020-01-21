export interface PaymentOrder {
  id: number;
  dateTime: string;
  productOrders: ProductOrder[];
  total: number;
}

export interface ProductOrder {
  id: number;
  productName: string;
  productPrice: number;
  quantity: number;
  total: number;
}