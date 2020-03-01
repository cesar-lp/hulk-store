import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { MaterialModule } from './material/material.module';
import {
  ProductListComponent,
  ProductComponent,
  ProductTypeListComponent,
  ProductTypeDialogComponent,
  ProductOrderComponent,
  ProductOrderLinesDialogComponent,
  ProductOrderListComponent,
} from './products/components';
import { ConfirmationDialogComponent, FileDownloaderComponent } from './common/components';
import { GlobalHTTPInterceptor } from './core/interceptors';

const MAT_SNACK_BAR_DEFAULT_OPTIONS_VALUE = {
  duration: 2500,
  horizontalPosition: 'right',
  verticalPosition: 'top'
};

@NgModule({
  declarations: [
    AppComponent,
    ConfirmationDialogComponent,
    FileDownloaderComponent,
    ProductComponent,
    ProductListComponent,
    ProductOrderComponent,
    ProductOrderLinesDialogComponent,
    ProductOrderListComponent,
    ProductTypeListComponent,
    ProductTypeDialogComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    MaterialModule,
    ReactiveFormsModule,
  ],
  providers: [
    { provide: MAT_DIALOG_DATA, useValue: {} },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: MAT_SNACK_BAR_DEFAULT_OPTIONS_VALUE },
    { provide: HTTP_INTERCEPTORS, useClass: GlobalHTTPInterceptor, multi: true }
  ],
  entryComponents: [
    ConfirmationDialogComponent,
    ProductTypeDialogComponent,
    ProductOrderLinesDialogComponent,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
