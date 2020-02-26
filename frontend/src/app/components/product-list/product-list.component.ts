import { OnInit, Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

import { ProductService } from 'src/app/services/product.service';
import { Product, ProductStockCondition } from 'src/app/models/product';
import { ConfirmationDialogComponent } from '../dialog/confirmation-dialog/confirmation-dialog.component';
import { NotificationService } from 'src/app/services/notification.service';
import { FileDownloadService } from './../../services/file-download.service';
import { FileType } from 'src/app/constants/file-type';

@Component({
  selector: 'product-list',
  templateUrl: './product-list.component.html',
  providers: [
    FileDownloadService,
    NotificationService,
    ProductService
  ]
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];
  displayedProductColumns: string[] = ['id', 'name', 'type', 'price', 'stock', 'actions'];
  fileFormats = [FileType.CSV, FileType.EXCEL];

  constructor(
    public dialog: MatDialog,
    private fileUtils: FileDownloadService,
    private productService: ProductService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit() {
    this.productService
      .getAllProducts()
      .subscribe(existingProducts => this.products = existingProducts);
  }

  handleDeleteProduct(id: number, name: string, action: string) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        dialogTitle: 'Delete Product',
        confirmationMessage: `Delete ${name}?`,
        primaryButtonText: 'Delete'
      }
    });

    dialogRef.afterClosed().subscribe(confirmation => {
      if (confirmation) {
        this.productService
          .deleteProductById(id)
          .subscribe(_ => this.removeProductFromExistingList(id));
      }
    });
  }

  download(fileType: FileType) {
    this.productService
      .download(fileType, ProductStockCondition.ALL)
      .subscribe(file => this.fileUtils.download(file, 'products.csv'));
  }

  handleProduct(id: number) {
    const path = id ? `${id}/edit` : 'new';
    this.router.navigate([path], { relativeTo: this.route });
  }

  private removeProductFromExistingList(id: number) {
    const i = this.products.findIndex(p => p.id == id);
    this.products.splice(i, 1);
    this.products = this.products.slice();
    this.notificationService.success('Product was removed successfully!');
  }
}