import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { RoleGuard } from '../../core/guards/role.guard';

export const areaTrabajoRoutes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/area-list/area-list.component').then(m => m.AreaListComponent),
        title: 'Áreas de Trabajo - SaaSRRHH'
      },
      {
        path: 'nuevo',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/area-form/area-form.component').then(m => m.AreaFormComponent),
        title: 'Nueva Área - SaaSRRHH'
      },
      {
        path: 'editar/:id',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/area-form/area-form.component').then(m => m.AreaFormComponent),
        title: 'Editar Área - SaaSRRHH'
      }
    ]
  }
];
