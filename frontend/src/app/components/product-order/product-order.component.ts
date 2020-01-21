import { Component } from '@angular/core';
import { Product } from 'src/app/models/product';
import { MatDialog } from '@angular/material/dialog';

import { ProductOrderService } from 'src/app/services/product-order.service';
import { ProductService } from 'src/app/services/product.service';
import { ConfirmationDialogComponent } from '../dialog/confirmation-dialog/confirmation-dialog.component';
import { Route } from '@angular/compiler/src/core';
import { Router } from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';
import { PaymentOrderRequest, ProductOrderRequest } from 'src/app/models/payment-order-request';
import { FormGroup, FormBuilder, FormArray, Validators } from '@angular/forms';

@Component({
  selector: 'product-order',
  templateUrl: './product-order.component.html',
  providers: [
    NotificationService,
    ProductService,
    ProductOrderService
  ]
})
export class ProductOrderComponent {

  availableProducts: Product[] = [];
  form: FormGroup;

  constructor(
    public dialog: MatDialog,
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private productService: ProductService,
    private productOrderService: ProductOrderService,
    private router: Router) { }

  ngOnInit() {
    this.form = this.fb.group({
      productOrders: this.fb.array([])
    });

    this.productService
      .getAllProducts()
      .subscribe(products => this.availableProducts = products);
  }

  addProductOrder() {
    const productOrder = this.fb.group({
      productId: [null, Validators.required],
      quantity: [null, [Validators.required, Validators.min(1)]],
    });

    (this.form.get('productOrders') as FormArray).push(productOrder);
    console.log(this.form);
  }

  createOrder() {
    const dialogRef = this.openConfirmationDialog('Create order', 'Create current order?', 'Create');

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        const orderRequest = this.mapFormToOrderRequest();
        this.productOrderService
          .createPaymentOrder(orderRequest)
          .subscribe(_ => {
            this.notificationService.success('Product order was successfully created!');
            this.router.navigate(['/product-orders'])
          });
      }
    });
  }

  cancel() {
    const dialogRef = this.openConfirmationDialog('Cancel order', 'Cancel current order?', 'Cancel');

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        this.router.navigate(['/product-orders'])
      }
    });
  }

  private mapFormToOrderRequest(): PaymentOrderRequest {
    const productOrders: ProductOrderRequest[] = [];

    (this.form.get('productOrders') as FormArray).controls.forEach(fg => {
      const productOrder: ProductOrderRequest = {
        productId: fg.get('productId').value,
        quantity: fg.get('quantity').value
      };
      productOrders.push(productOrder);
    });

    return { productOrders };
  }

  private openConfirmationDialog(title: string, message: string, primaryButtonText: string) {
    return this.dialog.open(ConfirmationDialogComponent, {
      data: {
        dialogTitle: title,
        confirmationMessage: message,
        primaryButtonText: primaryButtonText
      }
    });
  }
}