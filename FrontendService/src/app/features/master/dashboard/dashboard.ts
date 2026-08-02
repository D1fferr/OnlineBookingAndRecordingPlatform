import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';

import { AppointmentGetDTO, AppointmentsStatus } from '../../../core/models/appointment';

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
  displayedColumns: string[] = ['time', 'client', 'duration', 'comment', 'status', 'actions'];

  selectedDate = signal<Date>(new Date());

  appointments = signal<AppointmentGetDTO[]>([
    {
      id: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
      providerId: 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
      startTime: '2026-08-02T09:00:00+03:00',
      endTime: '2026-08-02T09:45:00+03:00',
      clientName: 'John Doe',
      clientEmail: 'john@example.com',
      clientComment: 'Client asked for short sides and scissors on top',
      status: 'CONFIRMED'
    },
    {
      id: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
      providerId: 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
      startTime: '2026-08-02T10:30:00+03:00',
      endTime: '2026-08-02T11:15:00+03:00',
      clientName: 'Alex Smith',
      clientEmail: 'alex@example.com',
      clientComment: 'Beard trim and styling',
      status: 'PENDING'
    },
    {
      id: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
      providerId: 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
      startTime: '2026-08-02T11:30:00+03:00',
      endTime: '2026-08-02T13:00:00+03:00',
      clientName: 'Sarah Connor',
      clientEmail: 'sarah@example.com',
      clientComment: 'Prefers quiet space during haircut',
      status: 'CANCELLED'
    }
  ]);

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
    this.appointments.update(items =>
      items.map(item => item.id === id ? { ...item, status: newStatus } : item)
    );
  }
}
