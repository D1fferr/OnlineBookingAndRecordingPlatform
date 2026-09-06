import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-delete-account-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './delete-account-dialog.html',
  styleUrl: './delete-account-dialog.css'
})
export class DeleteAccountDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<DeleteAccountDialogComponent>);

  form: FormGroup = this.fb.group({
    confirmText: ['', [Validators.required, Validators.pattern(/^DELETE$/)]]
  });

  onConfirm(): void {
    if (this.form.valid) {
      this.dialogRef.close(true);
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
