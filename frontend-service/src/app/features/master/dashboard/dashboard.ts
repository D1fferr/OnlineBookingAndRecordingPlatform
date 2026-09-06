import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AppointmentGetDTO, AppointmentCancelledReasonDTO } from '../../../core/models/appointment';
import { AppointmentService } from '../../../core/services/appointment';
import { AuthService } from '../../../core/services/auth';
import { CancelDialogComponent } from '../appointments/cancel-dialog/cancel-dialog';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatInputModule,
    MatFormFieldModule,
    MatNativeDateModule,
    MatDialogModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {
  private appointmentService = inject(AppointmentService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = ['time', 'client', 'duration', 'comment', 'status', 'actions'];

  selectedDate = signal<Date>(new Date());
  providerId = signal<string | null>(this.authService.getProviderId());
  appointments = signal<AppointmentGetDTO[]>([]);

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    const page = 0;
    const appPerPage = 50;

    this.appointmentService.getAppointments(
      page,
      appPerPage,
      undefined,
      'startTime',
      'asc'
    ).subscribe({
      next: (response) => {
        this.appointments.set(response.dtoList || []);
      },
      error: (err) => console.error('Failed to load dashboard appointments', err)
    });
  }

  onDateChange(newDate: Date | null): void {
    if (newDate) {
      this.selectedDate.set(newDate);
      this.loadDashboardData();
    }
  }

  formatTime(isoString: string): string {
    if (!isoString) return '-';
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  getDuration(startTimeStr: string, endTimeStr: string): string {
    if (!startTimeStr || !endTimeStr) return '-';
    const start = new Date(startTimeStr).getTime();
    const end = new Date(endTimeStr).getTime();
    const minutes = Math.round((end - start) / (1000 * 60));
    return `${minutes} min`;
  }

  truncateComment(comment?: string): string {
    if (!comment) return '-';
    const words = comment.trim().split(/\s+/);
    if (words.length <= 5) return comment;
    return words.slice(0, 5).join(' ') + '...';
  }

  confirmAppointment(id: string): void {
    this.appointmentService.updateAppointmentStatus(id, 'CONFIRMED').subscribe({
      next: () => this.loadDashboardData(),
      error: (err) => console.error('Failed to confirm appointment', err)
    });
  }

  openCancelDialog(appointment: AppointmentGetDTO): void {
    const dialogRef = this.dialog.open(CancelDialogComponent, {
      width: '420px'
    });

    dialogRef.afterClosed().subscribe((result: AppointmentCancelledReasonDTO | null) => {
      if (result) {
        // Передаємо id самого запису (appointment.id), як вимагає сервіс
        this.appointmentService.cancelAppointment(appointment.id, result).subscribe({
          next: () => this.loadDashboardData(),
          error: (err) => console.error('Failed to cancel appointment', err)
        });
      }
    });
  }
}
