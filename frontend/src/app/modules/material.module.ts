import { NgModule } from '@angular/core';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatInputModule } from '@angular/material/input';

const modules = [
  MatToolbarModule,
  MatSidenavModule,
  MatIconModule,
  MatTableModule,
  MatButtonModule,
  MatDividerModule,
  MatListModule,
  MatDialogModule,
  MatSnackBarModule,
  MatCardModule,
  MatFormFieldModule,
  MatSelectModule,
  MatFormFieldModule,
  MatProgressSpinnerModule,
  MatInputModule,
];

@NgModule({
  imports: [modules],
  exports: [modules]
})
export class MaterialModule { }