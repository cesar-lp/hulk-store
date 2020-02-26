import { ProductType } from './product-type';

export interface Product {
  id: number;
  name: string;
  productType: ProductType;
  stock: number;
  price: number;

  isSelected: boolean;
}

export enum ProductStockCondition {
  ALL = 'all',
  AVAILABLE = 'available',
  UNAVAILABLE = 'unavailable'
}
