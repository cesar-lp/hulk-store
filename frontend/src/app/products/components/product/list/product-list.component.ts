import { OnInit, Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

import { NotificationService } from 'src/app/common/services';
import { ProductService } from 'src/app/products/services';
import { Product, ProductStockCondition } from 'src/app/products/models';
import { FileTypeHandler, FileType } from 'src/app/common/models';
import { ConfirmationDialogComponent } from 'src/app/common/components/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'product-list',
  templateUrl: './product-list.component.html',
  providers: [ProductService]
})
export class ProductListComponent implements OnInit {

  displayedProductColumns: string[] = ['id', 'name', 'type', 'price', 'stock', 'actions'];
  fileFormats = FileTypeHandler.formats;
  products: Product[] = [];

  constructor(
    public dialog: MatDialog,
    private productService: ProductService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.productService
      .getAllProducts()
      .subscribe(existingProducts => this.products = existingProducts);
  }

  handleDeleteProduct(id: number, name: string) {
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
      .subscribe();
  }

  handleProduct(id?: number) {
    const path = id ? `${id}/edit` : 'new';
    this.router.navigate([path], { relativeTo: this.route });
  }

  private removeProductFromExistingList(id: number) {
    const i = this.products.findIndex(p => p.id === id);
    this.products.splice(i, 1);
    this.products = this.products.slice();
    this.notificationService.success('Product was removed successfully!');
  }
}
