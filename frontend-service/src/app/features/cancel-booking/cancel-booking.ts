import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BookingService } from '../../core/services/booking';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cancel-booking',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cancel-booking.html',
  styleUrl: './cancel-booking.css'
})
export class CancelBooking implements OnInit {
  private route = inject(ActivatedRoute);
  private bookingService = inject(BookingService);

  token = signal<string | null>(null);
  isSubmitting = signal<boolean>(false);
  isCancelled = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const tokenParam = this.route.snapshot.queryParamMap.get('token');
    if (!tokenParam) {
      this.errorMessage.set('Invalid or missing cancellation token.');
    } else {
      this.token.set(tokenParam);
    }
  }

  onConfirmCancel(): void {
    const currentToken = this.token();
    if (!currentToken) return;

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.bookingService.cancelAppointment(currentToken).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.isCancelled.set(true);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        const msg = err.error?.message || 'Failed to cancel appointment. It may have already been cancelled.';
        this.errorMessage.set(msg);
      }
    });
  }
}
