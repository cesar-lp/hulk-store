import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {

  title = 'store-client';
  isDrawerOpen = false;

  constructor(private router: Router) { }

  toggleDrawer() {
    this.isDrawerOpen = !this.isDrawerOpen;
  }

  goHome() {
    this.router.navigate([""]);
  }

  navigateToProducts() {
    this.router.navigate(["/products"]);
  }

  navigateToProductTypes() {
    this.router.navigate(["/product-types"]);
  }

  navigateToOrders() {
    this.router.navigate(["/product-orders"]);
  }
}
