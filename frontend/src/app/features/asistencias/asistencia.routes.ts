import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
export const asistenciaRoutes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    loadComponent: () => import('./pages/asistencia-dashboard/asistencia-dashboard.component').then(m => m.AsistenciaDashboardComponent),
    title: 'Asistencia - SaaSRRHH'
  }
];