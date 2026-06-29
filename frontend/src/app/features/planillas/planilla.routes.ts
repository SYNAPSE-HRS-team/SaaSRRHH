import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { RoleGuard } from '../../core/guards/role.guard';

export const planillaRoutes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    loadComponent: () =>
      import('./pages/planilla-list/planilla-list.component').then(m => m.PlanillaListComponent),
    title: 'Planillas - SaaSRRHH'
  }
];
