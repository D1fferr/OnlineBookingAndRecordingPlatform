import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AppointmentGetDTO, AppointmentCancelledReasonDTO } from '../../../core/models/appointment';
import { AppointmentService } from '../../../core/services/appointment';
import { CancelDialogComponent } from './cancel-dialog/cancel-dialog';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [
    FormsModule,
    DatePipe,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatPaginatorModule,
    MatDialogModule
  ],
  templateUrl: './appointments.html',
  styleUrl: './appointments.css'
})
export class AppointmentsComponent implements OnInit {
  private appointmentService = inject(AppointmentService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = ['dateTime', 'client', 'duration', 'comment', 'status', 'actions'];

  searchQuery = signal<string>('');
  sortBy = signal<string>('createdAt');
  sortDir = signal<string>('desc');

  pageSize = signal<number>(8);
  pageIndex = signal<number>(0);
  totalElements = signal<number>(0);

  appointments = signal<AppointmentGetDTO[]>([]);

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.appointmentService.getAppointments(
      this.pageIndex(),
      this.pageSize(),
      this.searchQuery(),
      this.sortBy(),
      this.sortDir()
    ).subscribe({
      next: (response) => {
        this.appointments.set(response.dtoList || []);
        this.totalElements.set(response.totalElements || 0);
      },
      error: (err) => console.error('Failed to load appointments', err)
    });
  }

  onSearchChange(val: string): void {
    this.searchQuery.set(val);
    this.pageIndex.set(0);
    this.loadAppointments();
  }

  onSortByChange(val: string): void {
    this.sortBy.set(val);
    this.pageIndex.set(0);
    this.loadAppointments();
  }

  onSortDirChange(val: string): void {
    this.sortDir.set(val);
    this.pageIndex.set(0);
    this.loadAppointments();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadAppointments();
  }

  confirmAppointment(id: string): void {
    this.appointmentService.updateAppointmentStatus(id, 'CONFIRMED').subscribe({
      next: () => this.loadAppointments(),
      error: (err) => console.error('Failed to confirm appointment', err)
    });
  }

  openCancelDialog(appointment: AppointmentGetDTO): void {
    const dialogRef = this.dialog.open(CancelDialogComponent, {
      width: '420px'
    });

    dialogRef.afterClosed().subscribe((result: AppointmentCancelledReasonDTO | null) => {
      if (result) {
        this.appointmentService.cancelAppointment(appointment.id, result).subscribe({
          next: () => this.loadAppointments(),
          error: (err) => console.error('Failed to cancel appointment', err)
        });
      }
    });
  }

  getDuration(startTimeStr: string, endTimeStr: string): string {
    if (!startTimeStr || !endTimeStr) return '-';
    const start = new Date(startTimeStr).getTime();
    const end = new Date(endTimeStr).getTime();
    const minutes = Math.round((end - start) / (1000 * 60));
    return `${minutes} min`;
  }
}
