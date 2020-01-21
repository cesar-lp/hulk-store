import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PaymentOrderRequest } from '../models/payment-order-request';
import { Observable } from 'rxjs';
import { PaymentOrder } from '../models/payment-order';

@Injectable()
export class ProductOrderService {

  private baseUrl = 'http://localhost:8080/api/orders';

  constructor(private httpClient: HttpClient) { }

  createPaymentOrder(paymentOrder: PaymentOrderRequest): Observable<PaymentOrder> {
    return this.httpClient.post<PaymentOrder>(this.baseUrl, paymentOrder);
  }

  // TODO
  getAllProductOrders() {

  }
}
