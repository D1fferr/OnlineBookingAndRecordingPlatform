import {
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpRequest,
  HttpHandlerFn,
  HttpEvent
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, switchMap, throwError, BehaviorSubject, filter, take } from 'rxjs';
import { AuthService } from './auth';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.currentUserToken();

  let authReq = req;


  if (req.url.includes('/refresh') || req.url.includes('/login')) {
    authReq = req.clone({ withCredentials: true });
  } else if (token && !req.url.includes('/public/')) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse): Observable<HttpEvent<unknown>> => {
      if (
        error.status === 401 &&
        !req.url.includes('/refresh') &&
        !req.url.includes('/login')
      ) {
        return handle401Error(authReq, next, authService, router);
      }

      return throwError(() => error);
    })
  );
};

function handle401Error(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  router: Router
): Observable<HttpEvent<unknown>> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((response) => {
        isRefreshing = false;

        if (response?.accessToken) {
          authService.saveToken(response.accessToken);
          refreshTokenSubject.next(response.accessToken);
        }

        const newReq = req.clone({
          setHeaders: {
            Authorization: `Bearer ${response.accessToken}`
          }
        });
        return next(newReq);
      }),
      catchError((refreshError) => {
        isRefreshing = false;
        authService.clearLocalSession();
        router.navigate(['/login']);
        return throwError(() => refreshError);
      })
    );
  } else {
    return refreshTokenSubject.pipe(
      filter((token): token is string => token !== null),
      take(1),
      switchMap((token) => {
        const newReq = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        });
        return next(newReq);
      })
    );
  }
}
