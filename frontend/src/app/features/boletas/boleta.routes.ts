import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';

export const boletaRoutes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./pages/boleta-list/boleta-list.component').then(m => m.BoletaListComponent),
    title: 'Boletas de Pago - SaaSRRHH'
  }
];
