import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { createFileWrapper } from './../common/utils/file-utils';
import { ProductType } from '../models/product-type';
import { FileType } from '../common/models/file-type-handler';
import { FileWrapper } from '../common/models/file-wrapper';

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

  download(fileType: FileType): Observable<FileWrapper> {
    const url = `${this.baseUrl}/export?format=${fileType.format}`;

    return this.httpService.get<Blob>(url, {
      observe: 'response',
      responseType: 'blob' as 'json'
    }).pipe(
      map(response => createFileWrapper(response)));
  }
}
