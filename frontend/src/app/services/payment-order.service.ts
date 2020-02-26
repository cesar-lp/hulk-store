import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { PaymentOrderRequest } from '../models/payment-order-request';
import { PaymentOrder } from '../models/payment-order';
import { FileType } from '../common/models/file-type-handler';
import { FileWrapper } from '../common/models/file-wrapper';
import { map } from 'rxjs/operators';
import { createFileWrapper } from '../common/utils/file-utils';

@Injectable()
export class PaymentOrderService {

  private baseUrl = 'http://localhost:8080/api/orders';

  constructor(private httpClient: HttpClient) { }

  createPaymentOrder(paymentOrder: PaymentOrderRequest): Observable<PaymentOrder> {
    return this.httpClient.post<PaymentOrder>(this.baseUrl, paymentOrder);
  }

  getAllPaymentOrders(): Observable<PaymentOrder[]> {
    return this.httpClient.get<PaymentOrder[]>(this.baseUrl);
  }

  download(fileType: FileType): Observable<FileWrapper> {
    const url = `${this.baseUrl}/export?format=${fileType.format}`;

    return this.httpClient.get<Blob>(url, {
      observe: 'response',
      responseType: 'blob' as 'json'
    }).pipe(
      map(response => createFileWrapper(response)));
  }
}
