import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';

import { UserService } from '../../../core/services/user';
import { UserForGetRequestDTO, UserQueryParams } from '../../../core/models/user';
import { BlockUserDialogComponent } from './block-user-dialog/block-user-dialog';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatSnackBarModule,
    MatDialogModule,
    MatSelectModule
  ],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);

  users = signal<UserForGetRequestDTO[]>([]);
  totalElements = signal<number>(0);
  isLoading = signal<boolean>(false);

  // Фільтри та пагінація
  searchQuery = signal<string>('');
  currentPage = signal<number>(0);
  pageSize = signal<number>(8);
  sortBy = signal<string>('createdAt');
  sortDir = signal<string>('desc');

  displayedColumns: string[] = ['id', 'user', 'role', 'status', 'blockReason', 'actions'];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);

    const params: UserQueryParams = {
      page: this.currentPage(),
      usersPerPage: this.pageSize(),
      sortBy: this.sortBy(),
      sortDir: this.sortDir(),
      search: this.searchQuery()
    };

    this.userService.getAllUsers(params).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.users.set(res.dtos || []);
        this.totalElements.set(res.totalElements || 0);
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to load users', err);
        this.snackBar.open('Failed to load users list', 'Close', { duration: 3000 });
      }
    });
  }

  onSearch(): void {
    this.currentPage.set(0);
    this.loadUsers();
  }

  onPageChange(event: PageEvent): void {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadUsers();
  }

  openBlockModal(user: UserForGetRequestDTO): void {
    const dialogRef = this.dialog.open(BlockUserDialogComponent, {
      width: '480px',
      data: { user, action: 'block' }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result && result.confirmed) {
        this.executeBlockUser(user.id, result.reason);
      }
    });
  }

  openUnblockModal(user: UserForGetRequestDTO): void {
    const dialogRef = this.dialog.open(BlockUserDialogComponent, {
      width: '480px',
      data: { user, action: 'unblock' }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result && result.confirmed) {
        this.executeUnblockUser(user.id);
      }
    });
  }

  private executeBlockUser(userId: string, reason: string): void {
    this.userService.blockUser({ userId, reason }).subscribe({
      next: () => {
        this.snackBar.open('User successfully blocked', 'OK', { duration: 3000 });
        this.loadUsers();
      },
      error: (err) => {
        console.error('Failed to block user', err);
        this.snackBar.open('Failed to block user', 'Close', { duration: 3000 });
      }
    });
  }

  private executeUnblockUser(userId: string): void {
    this.userService.unblockUser(userId).subscribe({
      next: () => {
        this.snackBar.open('User successfully unblocked', 'OK', { duration: 3000 });
        this.loadUsers();
      },
      error: (err) => {
        console.error('Failed to unblock user', err);
        this.snackBar.open('Failed to unblock user', 'Close', { duration: 3000 });
      }
    });
  }
}
