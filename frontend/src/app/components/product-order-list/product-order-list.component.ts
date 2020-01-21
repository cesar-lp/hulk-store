import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'product-order-list',
  templateUrl: './product-order-list.component.html'
})
export class ProductOrderListComponent {

  displayedProductOrdersColumns = ["id", "date", "productName", "quantity", "price", "total"];

  constructor(private router: Router) { }

  createProductOrder() {
    this.router.navigate(['/product-orders/new']);
  }
}