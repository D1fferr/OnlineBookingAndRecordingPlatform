import { Routes } from '@angular/router';
import { authGuard } from './core/core/guards/auth';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/master/dashboard/dashboard').then(m => m.DashboardComponent),
    // canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  }
];
