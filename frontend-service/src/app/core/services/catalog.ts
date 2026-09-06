import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ProviderPageForGetClientRequestDTO,
  ProviderListServiceTypeDTO,
  ProviderForGetBookingRequestDTO
} from '../models/catalog';

@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/providers/public`;

  getProviders(
    page: number = 0,
    providersPerPage: number = 6,
    category?: string,
    search?: string
  ): Observable<ProviderPageForGetClientRequestDTO> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('providersPerPage', providersPerPage.toString());

    if (category && category.trim() !== '') {
      params = params.set('category', category.trim());
    }

    if (search && search.trim() !== '') {
      params = params.set('search', search.trim());
    }

    return this.http.get<ProviderPageForGetClientRequestDTO>(`${this.apiUrl}/get-providers`, { params });
  }

  getCategories(): Observable<ProviderListServiceTypeDTO> {
    return this.http.get<ProviderListServiceTypeDTO>(`${this.apiUrl}/get-categories`);
  }

  getProviderById(id: string): Observable<ProviderForGetBookingRequestDTO> {
    return this.http.get<ProviderForGetBookingRequestDTO>(`${this.apiUrl}/get-one/${id}`);
  }
}
