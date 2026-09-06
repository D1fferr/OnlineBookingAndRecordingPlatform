import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ServicePageDTO, ServiceGetDTO, ServiceCreateDTO, ServiceUpdateDTO } from '../models/service';

@Injectable({
  providedIn: 'root'
})
export class ServiceManagementService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/services`;

  getServices(
    providerId: string,
    page: number = 0,
    servicePerPage: number = 8,
    sortBy: string = 'createdAt',
    sortDir: string = 'desc'
  ): Observable<ServicePageDTO> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('servicePerPage', servicePerPage.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<ServicePageDTO>(`${this.apiUrl}/auth/get-services/${providerId}`, { params });
  }

  createService(dto: ServiceCreateDTO): Observable<ServiceGetDTO> {
    return this.http.post<ServiceGetDTO>(`${this.apiUrl}/auth/create`, dto);
  }

  updateService(id: string, dto: ServiceUpdateDTO): Observable<ServiceGetDTO> {
    return this.http.patch<ServiceGetDTO>(`${this.apiUrl}/auth/update/${id}`, dto);
  }

  deleteService(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/auth/delete/${id}`);
  }
}
