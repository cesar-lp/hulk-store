import { OnInit, Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

import { ProductService } from 'src/app/services/product.service';
import { Product } from 'src/app/models/product';
import { ConfirmationDialogComponent } from '../dialog/confirmation-dialog/confirmation-dialog.component';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'product-list',
  templateUrl: './product-list.component.html',
  providers: [
    ProductService,
    NotificationService
  ]
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];
  displayedProductColumns: string[] = ['id', 'name', 'type', 'price', 'stock', 'actions'];

  constructor(
    public dialog: MatDialog,
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
    let title = 'Delete Product';
    let msg = `Delete ${name}?`;

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        dialogTitle: title,
        confirmationMessage: msg,
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