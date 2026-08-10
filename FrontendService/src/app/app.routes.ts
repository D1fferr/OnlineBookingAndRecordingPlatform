import { Routes } from '@angular/router';
import { authGuard } from './core/core/guards/auth';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then(m => m.RegisterComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () => import('./features/auth/forgot-password/forgot-password').then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/master/dashboard/dashboard').then(m => m.DashboardComponent),
    // canActivate: [authGuard]
  },
  {
    path: 'appointments',
    loadComponent: () => import('./features/master/appointments/appointments').then(m => m.AppointmentsComponent),
    // canActivate: [authGuard]
  },
  {
    path: 'services',
    loadComponent: () => import('./features/master/services/services/services').then(m => m.ServicesComponent),
    // canActivate: [authGuard]
  },
  {
    path: 'working-hours',
    loadComponent: () => import('./features/master/working-hours/working-hours').then(m => m.WorkingHoursComponent),
    // canActivate: [authGuard]
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/master/profile/profile').then(m => m.ProfileComponent),
    // canActivate: [authGuard]
  },
  {
    path: 'catalog',
    loadComponent: () => import('./features/catalog/catalog').then(m => m.CatalogComponent)
  },
  {
    path: 'provider/:id',
    loadComponent: () => import('./features/provider-detail/provider-detail').then(m => m.ProviderDetailComponent)
  },
  {
    path: 'booking/:serviceId',
    loadComponent: () => import('./features/booking/booking').then(m => m.BookingComponent)
  },
  {
    path: 'provider/:id',
    loadComponent: () => import('./features/provider-detail/provider-detail').then(m => m.ProviderDetailComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
