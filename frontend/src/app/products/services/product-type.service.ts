import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';

import { ProductType } from '../models';
import { FileType } from 'src/app/common/models';

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

  download(fileType: FileType): Observable<HttpResponse<Blob>> {
    const url = `${this.baseUrl}/export`;

    let params = new HttpParams();
    params = params.append('format', fileType.format);

    return this.httpService.get<Blob>(url, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json'
    });
  }
}
