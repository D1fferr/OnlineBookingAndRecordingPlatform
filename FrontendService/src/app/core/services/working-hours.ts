import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ListWorkingHoursGetDTO, ListWorkingHoursCreateDTO } from '../models/working-hours';

@Injectable({
  providedIn: 'root'
})
export class WorkingHoursService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}`;

  getWorkingHours(providerId: string): Observable<ListWorkingHoursGetDTO> {
    return this.http.get<ListWorkingHoursGetDTO>(`${this.apiUrl}/public/get-working-hours/${providerId}`);
  }

  setWorkingHours(dto: ListWorkingHoursCreateDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/set-working-hours`, dto);
  }
}
