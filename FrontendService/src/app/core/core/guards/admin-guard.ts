import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { catchError, map, of } from 'rxjs';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated() && authService.isAdmin()) {
    return true;
  }

  return authService.refreshToken().pipe(
    map((res) => {
      if (res?.accessToken && authService.isAdmin()) {
        return true;
      }
      router.navigate(['/catalog']);
      return false;
    }),
    catchError(() => {
      authService.clearLocalSession();
      router.navigate(['/login']);
      return of(false);
    })
  );
};
