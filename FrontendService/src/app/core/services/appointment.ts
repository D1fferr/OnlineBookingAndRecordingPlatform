import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AppointmentPageDTO, AppointmentsStatus, AppointmentCancelledReasonDTO } from '../models/appointment';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/appointments`;
  private authService = inject(AuthService);

  getAppointments(
    page: number,
    size: number,
    searchQuery?: string,
    status?: string,
    date?: Date | null
  ): Observable<AppointmentPageDTO> {
    const providerId = this.authService.getProviderId();
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (searchQuery && searchQuery.trim()) {
      params = params.set('search', searchQuery.trim());
    }

    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    if (date) {
      const formattedDate = date.toISOString().split('T')[0];
      params = params.set('date', formattedDate);
    }

    return this.http.get<AppointmentPageDTO>(`${this.apiUrl}/auth/get-appointments-by-provider/${providerId}`, { params });
  }

  updateAppointmentStatus(id: string, status: AppointmentsStatus): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/change-status-to-confirmed/${id}`, { status });
  }

  cancelAppointment(appointmentId: string, dto: AppointmentCancelledReasonDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/change-status-to-cancelled/${appointmentId}`, dto);
  }
}
