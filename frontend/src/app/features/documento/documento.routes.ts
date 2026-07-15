// documento.routes.ts
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
          import('./pages/documento-list/documento-list.component').then(
            (m) => m.DocumentoListComponent,
          ),
        title: 'Documentos - SaaSRRHH',
      },
      {
        path: 'nuevo',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] },
        loadComponent: () =>
          import('./pages/documento-form/documento-form.component').then(
            (m) => m.DocumentoFormComponent, // ← DEBE ser DocumentoFormComponent
          ),
        title: 'Nuevo Documento - SaaSRRHH',
      },
      {
        path: 'editar/:id',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] },
        loadComponent: () =>
          import('./pages/documento-form/documento-form.component').then(
            (m) => m.DocumentoFormComponent, // ← DEBE ser DocumentoFormComponent
          ),
        title: 'Editar Documento - SaaSRRHH',
      },
      {
        path: 'tipos-documento',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] },
        loadComponent: () =>
          import('./pages/tipo-documento-list/tipo-documento-list.component').then(
            (m) => m.TipoDocumentoListComponent,
          ),
        title: 'Tipos de Documento - SaaSRRHH',
      },
    ],
  },
];
