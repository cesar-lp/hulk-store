import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { StoreHTTPInterceptor } from './interceptors/http-interceptor.service';
import { ConfirmationDialogComponent } from './components/dialog/confirmation-dialog/confirmation-dialog.component';
import { ProductComponent } from './components/product/product.component';
import { ProductTypeListComponent } from './components/product-type-list/product-type-list.component';
import { ProductOrderListComponent } from './components/product-order-list/product-order-list.component';
import { ProductTypeDialogComponent } from './components/product-type-modal/product-type-dialog.component';
import { MaterialModule } from './modules/material.module';
import { ProductOrderComponent } from './components/product-order/product-order.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductListComponent,
    ConfirmationDialogComponent,
    ProductComponent,
    ProductTypeListComponent,
    ProductTypeDialogComponent,
    ProductOrderListComponent,
    ProductOrderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    MaterialModule,
    AppRoutingModule
  ],
  providers: [
    { provide: MAT_DIALOG_DATA, useValue: {} },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 2500 } },
    { provide: HTTP_INTERCEPTORS, useClass: StoreHTTPInterceptor, multi: true }
  ],
  entryComponents: [
    ProductTypeDialogComponent,
    ConfirmationDialogComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
