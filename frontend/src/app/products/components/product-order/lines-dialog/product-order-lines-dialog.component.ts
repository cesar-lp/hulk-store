import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { ProductOrder, ProductOrderLine } from 'src/app/products/models';

@Component({
  selector: 'product-order-lines-dialog',
  templateUrl: './product-order-lines-dialog.component.html',
  styleUrls: ['./product-order-lines-dialog.component.css']
})
export class ProductOrderLinesDialogComponent {

  displayedColumns = ['product', 'price', 'quantity', 'total'];
  productOrderLines: ProductOrderLine[] = [];
  title: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: ProductOrder) {
    this.productOrderLines = data.productOrderLines;
    this.title = `Product order n° ${data.id}`;
  }

  close() { }
}
