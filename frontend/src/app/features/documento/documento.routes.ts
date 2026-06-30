import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { RoleGuard } from '../../core/guards/role.guard';

export const documentoRoutes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] },
        loadComponent: () =>
          import('./pages/documento-list/documento-list.component').then(m => m.DocumentoListComponent),
        title: 'Documentos - SaaSRRHH'
      },
      {
        path: 'nuevo',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] },
        loadComponent: () =>
          import('./pages/documento-form/documento-form.component').then(m => m.DocumentoFormComponent),
        title: 'Nuevo Documento - SaaSRRHH'
      }
    ]
  }
];