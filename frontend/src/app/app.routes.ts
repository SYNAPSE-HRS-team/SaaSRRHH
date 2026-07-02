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
      // 🚀 TAREAS - Agregado por Miguel
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

      // ========================================================
      // 🚀 REPORTES DE INCIDENTES - Agregado por Miguel
      // ========================================================
      {
        path: 'reportes-incidentes',
        loadChildren: () =>
          import('./features/reportes-incidentes/reporte-incidente.module').then(
            (m) => m.ReporteIncidenteModule,
          ),
        title: 'Reportes de Incidentes - SaaSRRHH',
      },

      {
        path: 'nomina',
        loadChildren: () => import('./features/nomina/nomina.routes').then((m) => m.nominaRoutes),
        title: 'Nómina - SaaSRRHH',
      },
      {
        path: 'planillas',
        loadChildren: () => import('./features/planillas/planilla.routes').then((m) => m.planillaRoutes),
        title: 'Planillas - SaaSRRHH',
      },
      {
        path: 'boletas',
        loadChildren: () => import('./features/boletas/boleta.routes').then((m) => m.boletaRoutes),
        title: 'Boletas de Pago - SaaSRRHH',
      },
      {
        path: 'documentos',
        loadChildren: () => import('./features/documento/documento.routes').then((m) => m.documentoRoutes),
        title: 'Documentos - SaaSRRHH',
      },

      // ========================================================
      // 👤 PERFIL - Agregado por Nancy
      // ========================================================
      {
        path: 'perfil',
        loadChildren: () => import('./features/perfil/perfil.routes').then((m) => m.PERFIL_ROUTES),
        title: 'Mi Perfil - SaaSRRHH',
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