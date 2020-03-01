import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';

import { NotificationService } from 'src/app/common/services';
import { ProductService, ProductTypeService } from '../../services';
import { ProductType, Product, ProductRequest } from '../../models';
import { ConfirmationDialogComponent } from 'src/app/common/components/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'product',
  templateUrl: './product.component.html',
  providers: [ProductService, ProductTypeService]
})
export class ProductComponent implements OnInit {

  mainButtonText = 'Create';
  productForm: FormGroup;
  productTypes: ProductType[] = [];
  title = 'Create Product';

  constructor(
    private fb: FormBuilder,
    public dialog: MatDialog,
    private notificationService: NotificationService,
    private productService: ProductService,
    private productTypeService: ProductTypeService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.buildForm();

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (id) {
      this.title = 'Edit Product';
      this.mainButtonText = 'Edit';
      this.productService
        .getProductById(id)
        .subscribe(p => this.fillForm(p));
    }

    this.productTypeService
      .getAllProductTypes()
      .subscribe(types => this.productTypes = types);
  }

  handleProduct() {
    const id = this.productForm.get('id').value;
    const isCreatingProduct = id == null;
    const successMessage = `Product successfully ${isCreatingProduct ? 'created' : 'updated'}!`;

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        dialogTitle: isCreatingProduct ? 'Create Product' : 'Update Product',
        confirmationMessage: isCreatingProduct ? 'Confirm creation of Product?' : 'Confirm updating Product?',
        primaryButtonText: 'Confirm'
      }
    });

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        const productRequest = this.mapFormToProductRequest();

        const operation = isCreatingProduct
          ? this.productService.createProduct(productRequest)
          : this.productService.updateProduct(id, productRequest);

        operation.subscribe(_ => {
          this.notificationService.success(successMessage);
          this.router.navigate(['']);
        });
      }
    });
  }

  handleReturnToPreviousPage() {
    const isCreatingProduct = this.productForm.get('id') == null;

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        dialogTitle: isCreatingProduct ? 'Cancel new product' : 'Cancel product edition',
        confirmationMessage: 'Discard operation and return to previous page?',
        primaryButtonText: 'Return'
      }
    });

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        this.router.navigate(['']);
      }
    });
  }

  private buildForm() {
    this.productForm = this.fb.group({
      id: null,
      name: [null, Validators.required],
      productTypeId: [null, Validators.required],
      price: [null, [Validators.required, Validators.min(0)]],
      stock: [null, [Validators.required, Validators.min(0)]]
    });
  }

  private fillForm(p: Product) {
    this.productForm.setValue({
      id: p.id,
      name: p.name,
      productTypeId: p.productType.id,
      price: p.price,
      stock: p.stock
    });
  }

  private mapFormToProductRequest(): ProductRequest {
    return {
      id: null,
      name: this.productForm.get('name').value,
      productTypeId: this.productForm.get('productTypeId').value,
      price: this.productForm.get('price').value,
      stock: this.productForm.get('stock').value
    };
  }
}
