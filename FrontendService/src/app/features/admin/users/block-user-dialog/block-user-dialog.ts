import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { UserForGetRequestDTO } from '../../../../core/models/user';

export interface BlockUserDialogData {
  user: UserForGetRequestDTO;
  action: 'block' | 'unblock';
}

@Component({
  selector: 'app-block-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './block-user-dialog.html',
  styleUrl: './block-user-dialog.css'
})
export class BlockUserDialogComponent {
  dialogRef = inject(MatDialogRef<BlockUserDialogComponent>);
  data: BlockUserDialogData = inject(MAT_DIALOG_DATA);

  reason = '';

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onConfirm(): void {
    if (this.data.action === 'block' && !this.reason.trim()) {
      return;
    }
    this.dialogRef.close({
      confirmed: true,
      reason: this.reason.trim()
    });
  }
}
