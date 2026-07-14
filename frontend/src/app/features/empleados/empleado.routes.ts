import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { RoleGuard } from '../../core/guards/role.guard';

export const empleadoRoutes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] },
        loadComponent: () =>
          import('./pages/empleado-list/empleado-list.component').then(
            (m) => m.EmpleadoListComponent,
          ),
        title: 'Empleados - SaaSRRHH',
      },
      {
        path: 'nuevo',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] },
        loadComponent: () =>
          import('./pages/empleado-form/empleado-form.component').then(
            (m) => m.EmpleadoFormComponent,
          ),
        title: 'Nuevo Empleado - SaaSRRHH',
      },

      {
        path: 'editar/:id',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/empleado-form/empleado-form.component').then(
            (m) => m.EmpleadoFormComponent,
          ),
        title: 'Editar Empleado - SaaSRRHH',
      },
      {
        path: ':id',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/empleado-form/empleado-form.component').then(
            (m) => m.EmpleadoFormComponent,
          ),
        title: 'Editar Empleado - SaaSRRHH',
      },
    ],
  },
];
