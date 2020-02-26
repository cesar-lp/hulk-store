import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import { FileType } from './../../constants/file-type';
import { FileDownloadService } from './../../services/file-download.service';
import { ProductTypeService } from 'src/app/services/product-type.service';
import { ProductType } from 'src/app/models/product-type';
import { ConfirmationDialogComponent } from '../dialog/confirmation-dialog/confirmation-dialog.component';
import { NotificationService } from 'src/app/services/notification.service';
import { ProductTypeDialogComponent } from '../product-type-modal/product-type-dialog.component';

@Component({
  selector: 'product-type-list',
  templateUrl: './product-type-list.component.html',
  providers: [
    FileDownloadService,
    NotificationService,
    ProductTypeService
  ]
})
export class ProductTypeListComponent implements OnInit {

  productTypes: ProductType[] = [];
  displayedProductTypeColumns: string[] = ['id', 'name', 'actions'];
  fileFormats = [FileType.CSV, FileType.EXCEL];

  constructor(
    public dialog: MatDialog,
    private fileUtils: FileDownloadService,
    private notificationService: NotificationService,
    private productTypeService: ProductTypeService) { }

  ngOnInit() {
    this.productTypeService
      .getAllProductTypes()
      .subscribe(types => this.productTypes = types);
  }

  createProductType() {
    const dialog = this.dialog.open(ProductTypeDialogComponent, {
      data: {
        title: 'New product type',
        primaryButtonText: 'Create'
      }
    });

    dialog
      .afterClosed()
      .subscribe((createdProductType: ProductType) => {
        if (createdProductType) {
          this.productTypes.push(createdProductType);
          this.productTypes = this.productTypes.slice();
        }
      });
  }

  editProductType(productType: ProductType) {
    const dialog = this.dialog.open(ProductTypeDialogComponent, {
      data: {
        productType,
        title: 'Edit product type',
        primaryButtonText: 'Edit'
      }
    });

    dialog
      .afterClosed()
      .subscribe((updatedProductType: ProductType) => {
        if (updatedProductType) {
          const i = this.productTypes.findIndex(type => type.id == updatedProductType.id);
          this.productTypes[i] = updatedProductType;
          this.productTypes = this.productTypes.slice();
        }
      });
  }

  deleteProductType(id: number) {
    const message = 'Delete product type? This will also remove all associated products';

    const dialogRef = this.openDialog(ConfirmationDialogComponent, 'Delete product type', 'Delete', message);

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        this.productTypeService
          .deleteProductTypeById(id)
          .subscribe(_ => this.removeProductTypeFromList(id));
      }
    });
  }

  download(fileType: FileType) {
    this.productTypeService
      .download(fileType)
      .subscribe(file => this.fileUtils.download(file, 'product_types.csv'));
  }

  private openDialog(dialogComponent: any, title: string, primaryButtonText: string, message?: string) {
    return this.dialog.open(dialogComponent, {
      data: {
        dialogTitle: title,
        confirmationMessage: message,
        primaryButtonText
      }
    });
  }

  private removeProductTypeFromList(id: number) {
    const i = this.productTypes.findIndex(type => type.id == id);
    this.productTypes.splice(i, 1)
    this.productTypes = this.productTypes.slice();
    this.notificationService.success('Product type was removed successfully!');
  }
}