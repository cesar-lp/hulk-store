import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ProductListComponent } from './components/product-list/product-list.component';
import { ProductComponent } from './components/product/product.component';
import { ProductTypeListComponent } from './components/product-type-list/product-type-list.component';
import { ProductOrderListComponent } from './components/product-order-list/product-order-list.component';
import { ProductOrderComponent } from './components/product-order/product-order.component';

const routes: Routes = [
  {
    path: 'products',
    component: ProductListComponent
  },
  {
    path: "products/:id/edit",
    component: ProductComponent
  },
  {
    path: 'products/new',
    component: ProductComponent
  },
  { path: 'product-types', component: ProductTypeListComponent },
  { path: 'product-orders', component: ProductOrderListComponent },
  { path: 'product-orders/new', component: ProductOrderComponent },
  {
    path: '',
    redirectTo: '/products',
    pathMatch: 'full'
  },
  { path: '**', component: ProductComponent }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
