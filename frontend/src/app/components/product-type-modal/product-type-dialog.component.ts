import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ProductTypeService } from 'src/app/services/product-type.service';
import { NotificationService } from 'src/app/services/notification.service';
import { ProductType } from 'src/app/models/product-type';

@Component({
  selector: 'product-type-dialog',
  templateUrl: './product-type-dialog.component.html',
  providers: [NotificationService, ProductTypeService]
})
export class ProductTypeDialogComponent implements OnInit {

  form: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<ProductTypeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { productType: ProductType, title: string, primaryButtonText: string },
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private productTypeService: ProductTypeService) {
  }

  ngOnInit() {
    this.form = this.fb.group({
      id: null,
      name: [null, Validators.required]
    });

    if (this.data.productType) {
      this.form.get('id').setValue(this.data.productType.id);
      this.form.get('name').setValue(this.data.productType.name);
    }
  }

  cancel() {
    this.dialogRef.close(null);
  }

  saveProductType() {
    const productType = {
      id: this.form.get('id').value,
      name: this.form.get('name').value
    };

    const notificationMsg = `Product type was successfully ${productType.id ? 'updated' : 'created'}!`;

    const httpCall = productType.id
      ? this.productTypeService.updateProductType(productType.id, productType)
      : this.productTypeService.createProductType(productType);

    httpCall.subscribe(createdProductType => {
      this.notificationService.success(notificationMsg);
      this.dialogRef.close(createdProductType);
    });
  }
}
