import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Product, ProductStockCondition, ProductRequest } from '../models';
import { FileType } from 'src/app/common/models';

@Injectable()
export class ProductService {

  private baseUrl = 'http://localhost:8080/api/products';

  constructor(private httpService: HttpClient) { }

  getAllProducts(): Observable<Product[]> {
    return this.httpService.get<Product[]>(this.baseUrl);
  }

  getAvailableProducts(): Observable<Product[]> {
    let params = new HttpParams();
    params = params.append('stock', ProductStockCondition.AVAILABLE);
    return this.httpService.get<Product[]>(this.baseUrl, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.httpService.get<Product>(`${this.baseUrl}/${id}`);
  }

  createProduct(newProductRequest: ProductRequest): Observable<Product> {
    return this.httpService.post<Product>(this.baseUrl, newProductRequest);
  }

  updateProduct(id: number, updatedProductRequest: ProductRequest): Observable<Product> {
    return this.httpService.put<Product>(`${this.baseUrl}/${id}`, updatedProductRequest);
  }

  deleteProductById(id: number): Observable<void> {
    return this.httpService.delete<void>(`${this.baseUrl}/${id}`);
  }

  download(fileType: FileType, stockCondition: ProductStockCondition): Observable<HttpResponse<Blob>> {
    const url = `${this.baseUrl}/export`;

    let params = new HttpParams();
    params = params.append('format', fileType.format);
    params = params.append('stock', stockCondition);

    return this.httpService.get<Blob>(url, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json'
    });
  }
}
