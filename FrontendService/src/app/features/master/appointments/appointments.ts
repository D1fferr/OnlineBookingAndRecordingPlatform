import { Component, inject, signal, effect } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AppointmentGetDTO, AppointmentsStatus, AppointmentCancelledReasonDTO } from '../../../core/models/appointment';
import { AppointmentService } from '../../../core/services/appointment';
import { AuthService } from '../../../core/services/auth';
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
    MatDatepickerModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatDialogModule
  ],
  templateUrl: './appointments.html',
  styleUrl: './appointments.css'
})
export class AppointmentsComponent {
  private appointmentService = inject(AppointmentService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = ['dateTime', 'client', 'duration', 'comment', 'status', 'actions'];

  searchQuery = signal<string>('');
  selectedStatus = signal<string>('ALL');
  selectedDate = signal<Date | null>(null);

  pageSize = signal<number>(10);
  pageIndex = signal<number>(0);
  totalElements = signal<number>(0);

  providerId = signal<string | null>(this.authService.getProviderId());

  appointments = signal<AppointmentGetDTO[]>([]);

  constructor() {
    effect(() => {
      this.loadAppointments();
    });
  }

  loadAppointments(): void {
    this.appointmentService.getAppointments(
      this.pageIndex(),
      this.pageSize(),
      this.searchQuery(),
      this.selectedStatus(),
      this.selectedDate()
    ).subscribe({
      next: (response) => {
        this.appointments.set(response.dtoList);
        this.totalElements.set(response.totalElements);
      },
      error: (err) => console.error('Failed to load appointments', err)
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  confirmAppointment(id: string): void {
    this.appointmentService.updateAppointmentStatus(id, 'CONFIRMED').subscribe({
      next: () => this.loadAppointments(),
      error: (err) => console.error('Failed to confirm appointment', err)
    });
  }

  openCancelDialog(appointment: AppointmentGetDTO): void {
    const currentProviderId = this.providerId() || appointment.providerId;

    const dialogRef = this.dialog.open(CancelDialogComponent, {
      width: '420px'
    });

    dialogRef.afterClosed().subscribe((result: AppointmentCancelledReasonDTO | null) => {
      if (result) {
        this.appointmentService.cancelAppointment(appointment.id, currentProviderId, result).subscribe({
          next: () => this.loadAppointments(),
          error: (err) => console.error('Failed to cancel appointment', err)
        });
      }
    });
  }

  clearDateFilter(): void {
    this.selectedDate.set(null);
  }

  getDuration(startTimeStr: string, endTimeStr: string): string {
    if (!startTimeStr || !endTimeStr) return '-';
    const start = new Date(startTimeStr).getTime();
    const end = new Date(endTimeStr).getTime();
    const minutes = Math.round((end - start) / (1000 * 60));
    return `${minutes} min`;
  }
}
