import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { RoleGuard } from '../../core/guards/role.guard';

export const nominaRoutes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    loadComponent: () =>
      import('./pages/nomina/nomina.component').then(m => m.NominaComponent),
    title: 'Nómina - SaaSRRHH'
  }
];
