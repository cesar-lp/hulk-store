import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ProductType } from '../models/product-type';
import { FileType } from './../constants/file-type';

@Injectable()
export class ProductTypeService {

  private baseUrl = 'http://localhost:8080/api/product-types';

  constructor(private httpService: HttpClient) { }

  getAllProductTypes(): Observable<ProductType[]> {
    return this.httpService.get<ProductType[]>(this.baseUrl);
  }

  getProducTypeById(id: number): Observable<ProductType> {
    return this.httpService.get<ProductType>(`${this.baseUrl}/${id}`);
  }

  createProductType(newProductType: ProductType): Observable<ProductType> {
    return this.httpService.post<ProductType>(this.baseUrl, newProductType);
  }

  updateProductType(id: number, updatedProductType: ProductType): Observable<ProductType> {
    return this.httpService.put<ProductType>(`${this.baseUrl}/${id}`, updatedProductType);
  }

  deleteProductTypeById(id: number): Observable<void> {
    return this.httpService.delete<void>(`${this.baseUrl}/${id}`);
  }

  download(fileType: FileType): Observable<Blob> {
    return this.httpService.get(
      `${this.baseUrl}/export?format=${fileType}`,
      { responseType: 'blob' });
  }
}
