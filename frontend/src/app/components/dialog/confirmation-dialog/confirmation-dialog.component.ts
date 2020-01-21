import { Component, OnInit, Input, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html'
})
export class ConfirmationDialogComponent implements OnInit {

  dialogTitle: string;
  confirmationMessage: string;
  primaryButtonText: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: { dialogTitle: string, confirmationMessage: string, primaryButtonText: string }) {
    this.dialogTitle = data.dialogTitle;
    this.confirmationMessage = data.confirmationMessage;
    this.primaryButtonText = data.primaryButtonText
  }

  ngOnInit() {
  }

}