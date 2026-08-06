import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, finalize, of } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';
import { decodeJwtToken } from '../utils/jwt-decoder';
import {
  AuthResponseDTO,
  LoginDTO,
  RegistrationUserDTO,
  SendCodeDTO,
  ResetPasswordDTO
} from '../models/auth';

export type AuthResponse = AuthResponseDTO;
export type LoginRequest = LoginDTO;

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private jwtHelper = new JwtHelperService();

  private readonly TOKEN_KEY = 'jwt_token';
  private readonly API_URL = environment.apiUrl;

  currentUserToken = signal<string | null>(this.getTokenFromStorage());
  isAuthenticated = signal<boolean>(!this.isTokenExpired());

  login(request: LoginDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.API_URL}/public/login`, request).pipe(
      tap(response => {
        if (response?.accessToken) {
          this.saveToken(response.accessToken);
        }
      })
    );
  }

  register(dto: RegistrationUserDTO, file: File | null): Observable<AuthResponseDTO> {
    const formData = new FormData();

    const jsonBlob = new Blob([JSON.stringify(dto)], { type: 'application/json' });
    formData.append('userData', jsonBlob);

    if (file) {
      formData.append('imageData', file);
    }

    return this.http.post<AuthResponseDTO>(`${this.API_URL}/public/register`, formData).pipe(
      tap(response => {
        if (response?.accessToken) {
          this.saveToken(response.accessToken);
        }
      })
    );
  }

  sendResetCode(dto: SendCodeDTO): Observable<SendCodeDTO> {
    return this.http.post<SendCodeDTO>(`${this.API_URL}/reset-password/public/send-code`, dto);
  }

  resetPassword(dto: ResetPasswordDTO): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/reset-password/public/reset`, dto);
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/auth/logout`, {}).pipe(
      finalize(() => this.clearLocalSession()),
      catchError(() => of(void 0))
    );
  }

  clearLocalSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUserToken.set(null);
    this.isAuthenticated.set(false);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.currentUserToken.set(token);
    this.isAuthenticated.set(true);
  }

  setToken(token: string): void {
    this.saveToken(token);
  }

  getToken(): string | null {
    return this.getTokenFromStorage();
  }

  getTokenFromStorage(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getProviderId(): string | null {
    const token = this.getTokenFromStorage();
    if (!token) return null;

    const payload = decodeJwtToken(token);
    return payload?.['user_id'] || null;
  }

  private isTokenExpired(): boolean {
    const token = this.getTokenFromStorage();
    if (!token) return true;
    try {
      return this.jwtHelper.isTokenExpired(token);
    } catch {
      return true;
    }
  }

  getUserRole(): string | null {
    const token = this.getTokenFromStorage();
    if (!token) return null;
    const decodedToken = this.jwtHelper.decodeToken(token);
    return decodedToken?.role || decodedToken?.roles?.[0] || null;
  }
}
