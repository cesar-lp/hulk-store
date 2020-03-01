import { Component, ViewChild, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatTable } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';

import { NotificationService } from 'src/app/common/services';
import { ProductService, ProductOrderService } from '../../services';
import { Product, OrderLineRequest, ProductOrderRequest } from '../../models';
import { ConfirmationDialogComponent } from 'src/app/common/components/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'product-order',
  templateUrl: './product-order.component.html',
  styleUrls: ['./product-order.component.css'],
  providers: [ProductService, ProductOrderService]
})
export class ProductOrderComponent implements OnInit {

  availableProducts: Product[] = [];
  displayedColumns = ['product', 'price', 'stock', 'amount', 'total', 'actions'];
  form: FormGroup;
  remainingProductsToChoose: Product[][] = [];

  @ViewChild('ordersTable') ordersTable: MatTable<Element>;

  get productOrders() {
    return this.form.get('productOrders') as FormArray;
  }

  constructor(
    public dialog: MatDialog,
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private productService: ProductService,
    private paymentOrderService: ProductOrderService,
    private router: Router) { }

  ngOnInit() {
    this.form = this.fb.group({
      productOrders: this.fb.array([])
    });

    this.productService
      .getAvailableProducts()
      .subscribe(products => {
        products.forEach(p => p.isSelected = false);
        this.availableProducts = products;
      });
  }

  private getPreviousSelectedProductPosition(orderPosition: number) {
    const previousProductId = +this.productOrders.at(orderPosition).get('previousProductId').value;
    return this.availableProducts.findIndex(p => p.id === previousProductId);
  }

  onProductDeselected(orderPosition: number, handleEvent: boolean) {
    if (!handleEvent) {
      return;
    }

    const i = this.getPreviousSelectedProductPosition(orderPosition);
    this.availableProducts[i].isSelected = false;

    this.remainingProductsToChoose
      .filter((_, pos) => pos !== orderPosition)
      .forEach(remaining => remaining.push(this.availableProducts[i]));

    this.productOrders.at(orderPosition).patchValue({
      stock: '-',
      price: '-',
      total: '-'
    });
  }

  onProductSelected(id: number, orderPosition: number, handleEvent: boolean) {
    if (!handleEvent) {
      return;
    }

    const i = this.availableProducts.findIndex(p => p.id === id);
    const product = this.availableProducts[i];
    const deselectedProductPosition = this.getPreviousSelectedProductPosition(orderPosition);

    this.availableProducts[i].isSelected = true;

    if (deselectedProductPosition > -1) {
      this.availableProducts[deselectedProductPosition].isSelected = false;
    }

    this.productOrders.at(orderPosition).get('quantity').setValidators([
      Validators.required,
      Validators.min(1),
      Validators.max(product.stock)
    ]);
    this.productOrders.at(orderPosition).get('quantity').updateValueAndValidity();

    this.remainingProductsToChoose
      .filter((_, pos) => pos !== orderPosition)
      .forEach(remaining => {
        const remainingPos = remaining.findIndex(p => p.id === id);
        remaining.splice(remainingPos, 1);

        if (deselectedProductPosition > -1) {
          remaining.push(this.availableProducts[deselectedProductPosition]);
        }
      });

    const requestedQuantity = +this.productOrders.at(orderPosition).get('quantity').value;

    this.productOrders.at(orderPosition).patchValue({
      previousProductId: product ? product.id : null,
      stock: product.stock,
      price: product.price,
      total: requestedQuantity ? requestedQuantity * product.price : '-'
    });
  }

  onQuantityChange(orderPosition: number, quantity: number) {
    if (isNaN(quantity)) {
      this.productOrders.at(orderPosition).get('total').setValue('-');
      return;
    }

    if (this.productOrders.at(orderPosition).get('productId').value) {
      const price = +this.productOrders.at(orderPosition).get('price').value;
      this.productOrders.at(orderPosition).get('total').setValue(price * quantity);
    }
  }

  addProductOrder() {
    const productOrder = this.fb.group({
      previousProductId: null,
      productId: [null, Validators.required],
      price: { value: '-', disabled: true },
      stock: { value: '-', disabled: true },
      total: { value: '-', disabled: true },
      quantity: [null, [Validators.required, Validators.min(1)]],
    });

    const remainingProductsToChoose = this.availableProducts.filter(p => !p.isSelected);
    this.remainingProductsToChoose.push(remainingProductsToChoose);

    this.productOrders.push(productOrder);
    this.ordersTable.renderRows();
  }

  removeOrder(orderPosition: number) {
    this.remainingProductsToChoose.splice(orderPosition, 1);
    this.remainingProductsToChoose = this.remainingProductsToChoose.slice();

    // restore product to be available for other orders
    const selectedProductId = +this.productOrders.at(orderPosition).get('productId').value;

    if (selectedProductId) {
      const i = this.availableProducts.findIndex(p => p.id === selectedProductId);

      this.availableProducts[i].isSelected = false;

      this.remainingProductsToChoose.forEach(remainingProducts =>
        remainingProducts.push(this.availableProducts[i]));
    }

    this.productOrders.removeAt(orderPosition);
    this.ordersTable.renderRows();
  }

  createOrder() {
    const dialogRef = this.openConfirmationDialog('Create order', 'Create current order?', 'Create');

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        const orderRequest = this.mapFormToOrderRequest();
        this.paymentOrderService
          .createProductOrder(orderRequest)
          .subscribe(_ => {
            this.notificationService.success('Product order was successfully created!');
            this.router.navigate(['/product-orders']);
          });
      }
    });
  }

  cancel() {
    const dialogRef = this.openConfirmationDialog('Cancel order', 'Confirm cancelation of current order?', 'Confirm');

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        this.router.navigate(['/product-orders']);
      }
    });
  }

  private mapFormToOrderRequest(): ProductOrderRequest {
    const orderLines: OrderLineRequest[] = [];

    this.productOrders.controls.forEach(fg => {
      const orderLineRequest: OrderLineRequest = {
        productId: fg.get('productId').value,
        quantity: fg.get('quantity').value
      };
      orderLines.push(orderLineRequest);
    });

    return { orderLines };
  }

  private openConfirmationDialog(title: string, message: string, primaryButtonText: string) {
    return this.dialog.open(ConfirmationDialogComponent, {
      data: {
        dialogTitle: title,
        confirmationMessage: message,
        primaryButtonText
      }
    });
  }
}
