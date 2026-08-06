import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  UserForGetRequestDTO,
  ProviderForGetRequestDTO,
  ChangeCredentialsDTO,
  ProviderChangeDataDTO,
  AuthResponseDTO
} from '../models/profile';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private http = inject(HttpClient);
  private userUrl = `${environment.apiUrl}/user`;
  private providerUrl = `${environment.apiUrl}/providers`;

  getUser(id: string): Observable<UserForGetRequestDTO> {
    return this.http.get<UserForGetRequestDTO>(`${this.userUrl}/auth/get-one-user/${id}`);
  }

  getProvider(id: string): Observable<ProviderForGetRequestDTO> {
    return this.http.get<ProviderForGetRequestDTO>(`${this.providerUrl}/auth/get-one/${id}`);
  }

  getFullProfile(id: string): Observable<[UserForGetRequestDTO, ProviderForGetRequestDTO]> {
    return forkJoin([
      this.getUser(id),
      this.getProvider(id)
    ]);
  }

  changeCredentials(id: string, dto: ChangeCredentialsDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.userUrl}/auth/change-credentials/${id}`, dto);
  }

  deleteAccount(id: string): Observable<void> {
    return this.http.delete<void>(`${this.userUrl}/auth/delete/${id}`);
  }

  changeProfile(id: string, dto: ProviderChangeDataDTO): Observable<void> {
    return this.http.patch<void>(`${this.providerUrl}/auth/change-profile/${id}`, dto);
  }

  changeAvatar(id: string, file: File): Observable<void> {
    const formData = new FormData();
    formData.append('imageData', file);
    return this.http.post<void>(`${this.providerUrl}/auth/change-avatar/${id}`, formData);
  }
}
