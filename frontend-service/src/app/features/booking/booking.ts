import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

import { BookingService } from '../../core/services/booking';
import { ProviderForGetBookingRequestDTO, ServiceGetDTO } from '../../core/models/catalog';
import {
  DaySlotsDTO,
  FreeSlotDTO,
  AppointmentCreateDTO,
  AppointmentGetForCreateDTO
} from '../../core/models/booking';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './booking.html',
  styleUrl: './booking.css'
})
export class BookingComponent implements OnInit {
  private router = inject(Router);
  private location = inject(Location);
  private bookingService = inject(BookingService);

  isStateValid = signal<boolean>(true);
  service = signal<ServiceGetDTO | null>(null);
  provider = signal<ProviderForGetBookingRequestDTO | null>(null);

  availableDays = signal<DaySlotsDTO[]>([]);
  selectedDay = signal<DaySlotsDTO | null>(null);
  selectedSlot = signal<FreeSlotDTO | null>(null);

  clientName = '';
  clientEmail = '';
  clientComment = '';

  isLoadingSlots = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  createdAppointment = signal<AppointmentGetForCreateDTO | null>(null);

  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state || history.state;

    if (state && state.service && state.provider) {
      this.service.set(state.service);
      this.provider.set(state.provider);
      this.isStateValid.set(true);

      this.loadSlots(state.service.id);
    } else {
      this.isStateValid.set(false);
    }
  }

  loadSlots(serviceId: string): void {
    this.isLoadingSlots.set(true);
    this.bookingService.getFreeSlots(serviceId).subscribe({
      next: (res) => {
        this.isLoadingSlots.set(false);
        const days = res.appointments || [];
        this.availableDays.set(days);

        const firstDayWithSlots = days.find(d => d.freeSlots && d.freeSlots.length > 0);
        if (firstDayWithSlots) {
          this.selectedDay.set(firstDayWithSlots);
        } else if (days.length > 0) {
          this.selectedDay.set(days[0]);
        }
      },
      error: (err) => {
        this.isLoadingSlots.set(false);
        console.error('Failed to load slots', err);
        this.errorMessage.set('Failed to load available time slots. Please try again later.');
      }
    });
  }

  selectDay(day: DaySlotsDTO): void {
    this.selectedDay.set(day);
    this.selectedSlot.set(null);
  }

  selectSlot(slot: FreeSlotDTO): void {
    this.selectedSlot.set(slot);

    console.log('--- DEBUG SELECTED SLOT & DAY ---');
    console.log('Day date:', this.selectedDay()?.date, 'Type:', typeof this.selectedDay()?.date);
    console.log('Slot startTime:', slot.startTime, 'Type:', typeof slot.startTime);
    console.log('Slot endTime:', slot.endTime, 'Type:', typeof slot.endTime);
  }

  formatTime(value: string): string {
    if (!value) return '';
    if (value.includes('T')) {
      const date = new Date(value);
      return isNaN(date.getTime())
        ? value
        : date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
    }
    const parts = value.split(':');
    if (parts.length >= 2) {
      return `${parts[0]}:${parts[1]}`;
    }

    return value;
  }

  formatDateHeader(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
  }

  onSubmitBooking(): void {
    if (!this.selectedSlot() || !this.selectedDay() || !this.clientName?.trim() || !this.clientEmail?.trim()) {
      return;
    }

    const rawDate = this.selectedDay()!.date as any;
    let dateStr = '';

    if (Array.isArray(rawDate)) {
      const [year, month, day] = rawDate;
      const mm = String(month).padStart(2, '0');
      const dd = String(day).padStart(2, '0');
      dateStr = `${year}-${mm}-${dd}`;
    } else if (typeof rawDate === 'string') {
      dateStr = rawDate.split('T')[0];
    }

    const normalizeTime = (rawTime: any): string => {
      if (Array.isArray(rawTime)) {
        const [h, m, s = 0] = rawTime;
        return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
      }
      if (typeof rawTime === 'string') {
        return rawTime.split(':').length === 2 ? `${rawTime}:00` : rawTime;
      }
      return '00:00:00';
    };

    const startTimeStr = normalizeTime(this.selectedSlot()!.startTime);
    const endTimeStr = normalizeTime(this.selectedSlot()!.endTime);

    const startIso = `${dateStr}T${startTimeStr}`;
    const endIso = `${dateStr}T${endTimeStr}`;

    const startDate = new Date(startIso);
    const endDate = new Date(endIso);

    if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
      console.error('Failed to parse dates:', { dateStr, startTimeStr, endTimeStr, startIso });
      this.errorMessage.set('Invalid date or time slot selected.');
      return;
    }

    const payload: AppointmentCreateDTO = {
      providerId: this.provider()!.id,
      serviceId: this.service()!.id,
      startTime: startDate.toISOString(),
      endTime: endDate.toISOString(),
      clientName: this.clientName.trim(),
      clientEmail: this.clientEmail.trim(),
      clientComment: this.clientComment?.trim() || ''
    };

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.bookingService.createAppointment(payload).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.createdAppointment.set(res);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        console.error('Booking failed', err);
        const msg = err.error?.message || 'Selected time slot is no longer available. Please select another time.';
        this.errorMessage.set(msg);
      }
    });
  }

  goBack(): void {
    this.location.back();
  }

  goToCatalog(): void {
    this.router.navigate(['/catalog']);
  }
}
