import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.authRoutes),
    title: 'Autenticación - SaaSRRHH',
  },
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./features/dashboard/dashboard.routes').then((m) => m.dashboardRoutes),
        title: 'Dashboard - SaaSRRHH',
      },
      {
        path: 'empleados',
        loadChildren: () =>
          import('./features/empleados/empleado.routes').then((m) => m.empleadoRoutes),
        title: 'Empleados - SaaSRRHH',
      },
      {
        path: 'areas-trabajo',
        loadChildren: () =>
          import('./features/areas-trabajo/area-trabajo.routes').then((m) => m.areaTrabajoRoutes),
        title: 'Áreas de Trabajo - SaaSRRHH',
      },

      // ========================================================
      // 🚀 SECCIÓN DE TAREAS ASIGNADAS AGREGADA CON LAZY LOADING
      // ========================================================
      {
        path: 'tareas',
        loadChildren: () => import('./features/tareas/tarea.routes').then((m) => m.tareaRoutes),
        title: 'Tareas - SaaSRRHH',
      },
      {
        path: 'asistencias',
        loadChildren: () =>
          import('./features/asistencias/asistencia.routes').then((m) => m.asistenciaRoutes),
        title: 'Asistencia - SaaSRRHH',
      },
      {
        path: 'usuarios',
        loadChildren: () =>
          import('./features/usuarios/usuario.routes').then((m) => m.usuarioRoutes),
        title: 'Usuarios - SaaSRRHH',
      },
      {
        path: 'reportes-incidentes',
        loadChildren: () =>
          import('./features/reportes-incidentes/reporte-incidente.module').then(
            (m) => m.ReporteIncidenteModule,
          ),
        title: 'Reportes de Incidentes - SaaSRRHH',
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];
