import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { ServiceGetDTO } from '../../../../core/models/service';

export interface ServiceDialogData {
  service?: ServiceGetDTO;
}

@Component({
  selector: 'app-service-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './service-dialog.html',
  styleUrl: './service-dialog.css'
})
export class ServiceDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<ServiceDialogComponent>);
  public data: ServiceDialogData = inject(MAT_DIALOG_DATA);

  isEditMode = !!this.data?.service;

  form: FormGroup = this.fb.group({
    serviceName: [this.data?.service?.serviceName || '', [Validators.required]],
    duration: [this.data?.service?.duration || 30, [Validators.required, Validators.min(5)]],
    price: [this.data?.service?.price || 0, [Validators.required, Validators.min(0)]],
    description: [this.data?.service?.description || '', [Validators.required]]
  });

  onSave(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
