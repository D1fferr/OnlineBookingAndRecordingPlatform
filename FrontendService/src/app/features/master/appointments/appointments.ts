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

import { AppointmentGetDTO, AppointmentsStatus } from '../../../core/models/appointment';
import { AppointmentService } from '../../../core/services/appointment';

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
    MatPaginatorModule
  ],
  templateUrl: './appointments.html',
  styleUrl: './appointments.css'
})
export class AppointmentsComponent {
  private appointmentService = inject(AppointmentService);

  displayedColumns: string[] = ['dateTime', 'client', 'duration', 'comment', 'status', 'actions'];

  searchQuery = signal<string>('');
  selectedStatus = signal<string>('ALL');
  selectedDate = signal<Date | null>(null);

  pageSize = signal<number>(10);
  pageIndex = signal<number>(0);
  totalElements = signal<number>(0);

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

  updateStatus(id: string, newStatus: AppointmentsStatus): void {
    this.appointmentService.updateAppointmentStatus(id, newStatus).subscribe({
      next: () => this.loadAppointments(),
      error: (err) => console.error('Failed to update status', err)
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
