import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {
  ProductListComponent,
  ProductComponent,
  ProductTypeListComponent,
  ProductOrderListComponent,
  ProductOrderComponent
} from './products/components';

const routes: Routes = [
  {
    path: 'products',
    component: ProductListComponent
  },
  {
    path: 'products/:id/edit',
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
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
