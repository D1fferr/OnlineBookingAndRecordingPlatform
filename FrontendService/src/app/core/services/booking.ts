import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AvailableSlotsResponseDTO,
  AppointmentCreateDTO,
  AppointmentGetForCreateDTO
} from '../models/booking';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/appointments`;

  getFreeSlots(serviceId: string): Observable<AvailableSlotsResponseDTO> {
    return this.http.post<AvailableSlotsResponseDTO>(`${this.apiUrl}/public/get-free-slots/${serviceId}`, {});
  }

  createAppointment(dto: AppointmentCreateDTO): Observable<AppointmentGetForCreateDTO> {
    return this.http.post<AppointmentGetForCreateDTO>(`${this.apiUrl}/auth/create`, dto);
  }
}
