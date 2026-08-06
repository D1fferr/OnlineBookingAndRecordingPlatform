import { Component, inject, signal, effect } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { ProfileService } from '../../../core/services/profile';
import { AuthService } from '../../../core/services/auth';
import { UserForGetRequestDTO, ProviderForGetRequestDTO, ChangeCredentialsDTO, ProviderChangeDataDTO } from '../../../core/models/profile';
import { DeleteAccountDialogComponent } from './delete-account-dialog/delete-account-dialog';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent {
  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private router = inject(Router);

  userId = signal<string | null>(this.authService.getProviderId());
  userData = signal<UserForGetRequestDTO | null>(null);
  providerData = signal<ProviderForGetRequestDTO | null>(null);

  selectedFile: File | null = null;
  avatarPreview = signal<string | null>(null);

  timezones: string[] = [
    'Europe/Kyiv',
    'Europe/London',
    'Europe/Berlin',
    'America/New_York',
    'UTC'
  ];

  personalForm: FormGroup = this.fb.group({
    name: ['', [Validators.required]],
    serviceType: ['', [Validators.required]],
    timezone: ['Europe/Kyiv', [Validators.required]]
  });

  securityForm: FormGroup = this.fb.group({
    email: ['', [Validators.email]],
    password: [''],
    currentPassword: ['', [Validators.required]]
  });

  constructor() {
    effect(() => {
      this.loadFullProfileData();
    });
  }

  loadFullProfileData(): void {
    const id = this.userId();
    if (!id) return;

    this.profileService.getFullProfile(id).subscribe({
      next: ([user, provider]) => {
        this.userData.set(user);
        this.providerData.set(provider);

        this.personalForm.patchValue({
          name: provider.name,
          serviceType: provider.serviceType,
          timezone: provider.timezone || 'Europe/Kyiv'
        });

        this.securityForm.patchValue({
          email: user.email
        });

        if (user.avatarURL) {
          this.avatarPreview.set(user.avatarURL);
        }
      },
      error: (err) => console.error('Failed to load profile data', err)
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => this.avatarPreview.set(reader.result as string);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  uploadAvatar(): void {
    const id = this.userId();
    if (!id || !this.selectedFile) return;

    this.profileService.changeAvatar(id, this.selectedFile).subscribe({
      next: () => {
        this.snackBar.open('Avatar updated successfully!', 'Close', { duration: 3000 });
        this.selectedFile = null;
      },
      error: (err) => {
        console.error('Failed to upload avatar', err);
        this.snackBar.open('Failed to upload avatar', 'Close', { duration: 3000 });
      }
    });
  }

  savePersonalInfo(): void {
    const id = this.userId();
    if (!id || this.personalForm.invalid) return;

    const dto: ProviderChangeDataDTO = this.personalForm.value;
    this.profileService.changeProfile(id, dto).subscribe({
      next: () => {
        this.snackBar.open('Personal info updated successfully!', 'Close', { duration: 3000 });
        this.loadFullProfileData();
      },
      error: (err) => {
        console.error('Failed to update profile', err);
        this.snackBar.open('Failed to update personal info', 'Close', { duration: 3000 });
      }
    });
  }

  saveCredentials(): void {
    const id = this.userId();
    if (!id || this.securityForm.invalid) return;

    const dto: ChangeCredentialsDTO = this.securityForm.value;
    this.profileService.changeCredentials(id, dto).subscribe({
      next: (response) => {
        if (response && response.accessToken) {
          localStorage.setItem('jwt_token', response.accessToken);
        }
        this.snackBar.open('Credentials updated successfully!', 'Close', { duration: 3000 });
        this.securityForm.get('currentPassword')?.reset();
        this.securityForm.get('password')?.reset();
      },
      error: (err) => {
        console.error('Failed to update credentials', err);
        this.snackBar.open('Failed to update security credentials', 'Close', { duration: 3000 });
      }
    });
  }

  openDeleteDialog(): void {
    const id = this.userId();
    if (!id) return;

    const dialogRef = this.dialog.open(DeleteAccountDialogComponent, {
      width: '420px'
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.profileService.deleteAccount(id).subscribe({
          next: () => {
            this.authService.logout();
            this.router.navigate(['/login']);
          },
          error: (err) => {
            console.error('Failed to delete account', err);
            this.snackBar.open('Failed to delete account', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
}
