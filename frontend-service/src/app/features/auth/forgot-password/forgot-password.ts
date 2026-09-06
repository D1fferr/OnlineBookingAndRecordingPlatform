import { Component, inject, signal, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { AuthService } from '../../../core/services/auth';
import { SendCodeDTO, ResetPasswordDTO } from '../../../core/models/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css'
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private cdr = inject(ChangeDetectorRef);

  step = signal<number>(1);
  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  sendCodeForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  resetPasswordForm: FormGroup = this.fb.group({
    code: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSendCode(): void {
    if (this.sendCodeForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const dto: SendCodeDTO = this.sendCodeForm.value;

    this.authService.sendResetCode(dto).subscribe({
      next: (response) => {
        console.log('Send code response:', response);
        this.isLoading.set(false);
        this.step.set(2);
        this.cdr.detectChanges();
        this.snackBar.open('Verification code sent to your email!', 'Close', { duration: 4000 });
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to send reset code', err);
        const msg = err.error?.message || 'Failed to send code. Please check your email and try again.';
        this.errorMessage.set(msg);
        this.cdr.detectChanges();
      }
    });
  }

  onResetPassword(): void {
    if (this.resetPasswordForm.invalid) return;

    const emailValue = this.sendCodeForm.get('email')?.value;
    if (!emailValue) {
      this.errorMessage.set('Email is missing. Please start over.');
      this.step.set(1);
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const dto: ResetPasswordDTO = {
      email: emailValue,
      code: this.resetPasswordForm.get('code')?.value,
      newPassword: this.resetPasswordForm.get('newPassword')?.value
    };

    this.authService.resetPassword(dto).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.snackBar.open('Password successfully reset! Please login with your new password.', 'Close', { duration: 5000 });
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to reset password', err);
        const msg = err.error?.message || 'Invalid verification code or expired session. Please try again.';
        this.errorMessage.set(msg);
        this.cdr.detectChanges();
      }
    });
  }

  backToStepOne(): void {
    this.errorMessage.set(null);
    this.resetPasswordForm.reset();
    this.step.set(1);
    this.cdr.detectChanges();
  }
}
