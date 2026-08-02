import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, finalize, of } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';

export interface AuthResponse {
  token: string;
  refreshToken?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private jwtHelper = new JwtHelperService();

  private readonly TOKEN_KEY = 'jwt_token';
  private readonly AUTH_API_URL = `${environment.apiUrl}/auth`;

  currentUserToken = signal<string | null>(this.getTokenFromStorage());
  isAuthenticated = signal<boolean>(!this.isTokenExpired());

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.AUTH_API_URL}/login`, request).pipe(
      tap(response => {
        if (response.token) {
          this.saveToken(response.token);
        }
      })
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.AUTH_API_URL}/logout`, {}).pipe(
      finalize(() => this.clearLocalSession()),
      catchError(() => of(void 0))
    );
  }

  clearLocalSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUserToken.set(null);
    this.isAuthenticated.set(false);
  }

  private saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.currentUserToken.set(token);
    this.isAuthenticated.set(true);
  }

  getTokenFromStorage(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private isTokenExpired(): boolean {
    const token = this.getTokenFromStorage();
    if (!token) return true;
    return this.jwtHelper.isTokenExpired(token);
  }

  getUserRole(): string | null {
    const token = this.getTokenFromStorage();
    if (!token) return null;
    const decodedToken = this.jwtHelper.decodeToken(token);
    return decodedToken?.role || decodedToken?.roles?.[0] || null;
  }
}
