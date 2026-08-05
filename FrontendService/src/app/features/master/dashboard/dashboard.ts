import { Component, inject, signal, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';

import { AppointmentGetDTO, AppointmentsStatus } from '../../../core/models/appointment';
import { AppointmentService } from '../../../core/services/appointment';

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
    MatNativeDateModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent {
  private appointmentService = inject(AppointmentService);

  displayedColumns: string[] = ['time', 'client', 'duration', 'comment', 'status', 'actions'];

  selectedDate = signal<Date>(new Date());

  appointments = signal<AppointmentGetDTO[]>([]);

  constructor() {
    effect(() => {
      this.loadDashboardData();
    });
  }

  loadDashboardData(): void {
    const page = 0;
    const size = 50;

    this.appointmentService.getAppointments(
      page,
      size,
      undefined, // searchQuery
      undefined, // status
      this.selectedDate()
    ).subscribe({
      next: (response) => {
        this.appointments.set(response.dtoList);
      },
      error: (err) => console.error('Failed to load dashboard appointments', err)
    });
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

  updateStatus(id: string, newStatus: AppointmentsStatus): void {
    this.appointmentService.updateAppointmentStatus(id, newStatus).subscribe({
      next: () => this.loadDashboardData(),
      error: (err) => console.error('Failed to update status', err)
    });
  }
}
