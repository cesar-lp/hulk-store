import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ProductStockCondition } from './../models/product';
import { createFileWrapper } from './../common/utils/file-utils';
import { Product } from '../models/product';
import { ProductRequest } from '../models/product-request';
import { FileType } from '../common/models/file-type-handler';
import { FileWrapper } from '../common/models/file-wrapper';

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

  download(fileType: FileType, stockCondition: ProductStockCondition): Observable<FileWrapper> {
    const url = `${this.baseUrl}/export?format=${fileType.format}&stock=${stockCondition}`;

    return this.httpService.get<Blob>(url, { observe: 'response', responseType: 'blob' as 'json' })
      .pipe(
        map(response => createFileWrapper(response))
      );
  }
}
