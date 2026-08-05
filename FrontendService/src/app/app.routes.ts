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
    path: 'appointments',
    loadComponent: () => import('./features/master/appointments/appointments').then(m => m.AppointmentsComponent)
  },
  {
    path: 'services',
    loadComponent: () => import('./features/master/services/services/services').then(m => m.ServicesComponent),
    // canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: 'appointments',
    pathMatch: 'full'
  }
];
