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
    appPerPage: number,
    searchQuery?: string,
    sortBy: string = 'createdAt',
    sortDir: string = 'desc'
  ): Observable<AppointmentPageDTO> {
    const providerId = this.authService.getProviderId();

    let params = new HttpParams()
      .set('page', page.toString())
      .set('appPerPage', appPerPage.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    if (searchQuery && searchQuery.trim()) {
      params = params.set('search', searchQuery.trim());
    }

    return this.http.get<AppointmentPageDTO>(
      `${this.apiUrl}/auth/get-appointments-by-provider/${providerId}`,
      { params }
    );
  }

  updateAppointmentStatus(id: string, status: AppointmentsStatus): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/change-status-to-confirmed/${id}`, { status });
  }

  cancelAppointment(appointmentId: string, dto: AppointmentCancelledReasonDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/change-status-to-cancelled/${appointmentId}`, dto);
  }
}
