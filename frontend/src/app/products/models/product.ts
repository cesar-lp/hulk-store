import { ProductType } from './product-type';

export interface Product {
  id: number;
  name: string;
  productType: ProductType;
  productTypeName: string;
  stock: number;
  price: number;

  isSelected?: boolean;
}

export interface ProductRequest {
  id?: number;
  name: string;
  productTypeId: number;
  stock: number;
  price: number;
}

export enum ProductStockCondition {
  ALL = 'all',
  AVAILABLE = 'available',
  UNAVAILABLE = 'unavailable'
}
