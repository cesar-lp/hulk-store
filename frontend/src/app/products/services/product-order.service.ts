import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';

import { ProductOrderRequest, ProductOrder } from '../models';
import { FileType } from 'src/app/common/models';

@Injectable()
export class ProductOrderService {

  private baseUrl = 'http://localhost:8080/api/orders';

  constructor(private httpClient: HttpClient) { }

  createProductOrder(productOrder: ProductOrderRequest): Observable<ProductOrder> {
    return this.httpClient.post<ProductOrder>(this.baseUrl, productOrder);
  }

  getAllProductOrders(): Observable<ProductOrder[]> {
    return this.httpClient.get<ProductOrder[]>(this.baseUrl);
  }

  download(fileType: FileType): Observable<HttpResponse<Blob>> {
    const url = `${this.baseUrl}/export`;

    let params = new HttpParams();
    params = params.append('format', fileType.format);

    return this.httpClient.get<Blob>(url, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json'
    });
  }
}
