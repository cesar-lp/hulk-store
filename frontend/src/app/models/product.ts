import { ProductType } from './product-type';

export interface Product {
  id: number;
  name: string;
  productType: ProductType;
  stock: number;
  price: number;

  isSelected: boolean;
}