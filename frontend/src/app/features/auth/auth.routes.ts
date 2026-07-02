import { Routes } from '@angular/router';
import { AuthLayoutComponent } from '../../layouts/auth-layout/auth-layout.component';

export const authRoutes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./pages/login/login.component').then(m => m.LoginComponent),
        title: 'Iniciar Sesión - SaaSRRHH'
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./pages/register/register.component').then(m => m.RegisterComponent),
        title: 'Registro - SaaSRRHH'
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      }
    ]
  }
];
