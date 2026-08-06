import { Component, inject, signal } from '@angular/core';
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
      next: () => {
        this.isLoading.set(false);
        this.step.set(2);
        this.snackBar.open('Verification code sent to your email!', 'Close', { duration: 4000 });
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to send reset code', err);
        this.errorMessage.set('Failed to send code. Please check your email and try again.');
      }
    });
  }

  onResetPassword(): void {
    if (this.resetPasswordForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const dto: ResetPasswordDTO = {
      email: this.sendCodeForm.get('email')?.value,
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
        this.errorMessage.set('Invalid verification code or expired session. Please try again.');
      }
    });
  }

  backToStepOne(): void {
    this.errorMessage.set(null);
    this.step.set(1);
  }
}
