import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';

import { ProductOrderService } from 'src/app/products/services';
import { FileTypeHandler, FileType } from 'src/app/common/models';
import { ProductOrder } from 'src/app/products/models';
import { ProductOrderLinesDialogComponent } from '../lines-dialog/product-order-lines-dialog.component';

@Component({
  selector: 'product-order-list',
  templateUrl: './product-order-list.component.html',
  providers: [ProductOrderService]
})
export class ProductOrderListComponent implements OnInit {

  productOrders: ProductOrder[];
  displayedColumns = ['id', 'date', 'productsAmount', 'total', 'actions'];
  fileFormats = [FileTypeHandler.PDF];

  constructor(
    private dialog: MatDialog,
    private productOrderService: ProductOrderService,
    private router: Router) { }

  ngOnInit() {
    this.productOrderService
      .getAllProductOrders()
      .subscribe(results => this.productOrders = results);
  }

  createProductOrder() {
    this.router.navigate(['/product-orders/new']);
  }

  download(fileType: FileType) {
    this.productOrderService.download(fileType).subscribe();
  }

  viewProductOrderDetails(productOrder: ProductOrder) {
    this.dialog.open(ProductOrderLinesDialogComponent, { data: productOrder });
  }
}
