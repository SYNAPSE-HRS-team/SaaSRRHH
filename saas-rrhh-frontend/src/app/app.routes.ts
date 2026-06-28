import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes),
    title: 'AutenticaciÃ³n - SaaSRRHH'
  },
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.dashboardRoutes),
        title: 'Dashboard - SaaSRRHH'
      },
      {
        path: 'empleados',
        loadChildren: () => import('./features/empleados/empleado.routes').then(m => m.empleadoRoutes),
        title: 'Empleados - SaaSRRHH'
      },
      {
        path: 'areas-trabajo',
        loadChildren: () => import('./features/areas-trabajo/area-trabajo.routes').then(m => m.areaTrabajoRoutes),
        title: 'Ãreas de Trabajo - SaaSRRHH'
      },
      {
        path: 'asistencias',
        loadChildren: () => import('./features/asistencias/asistencia.routes').then(m => m.asistenciaRoutes),
        title: 'Asistencia - SaaSRRHH'
      },
      {
        path: 'usuarios',
        loadChildren: () => import('./features/usuarios/usuario.routes').then(m => m.usuarioRoutes),
        title: 'Usuarios - SaaSRRHH'
      },
      {
        path: 'nomina',
        loadChildren: () => import('./features/nomina/nomina.routes').then(m => m.nominaRoutes),
        title: 'Nómina - SaaSRRHH'
      },
      {
        path: 'planillas',
        loadChildren: () => import('./features/planillas/planilla.routes').then(m => m.planillaRoutes),
        title: 'Planillas - SaaSRRHH'
      },
      {
        path: 'boletas',
        loadChildren: () => import('./features/boletas/boleta.routes').then(m => m.boletaRoutes),
        title: 'Boletas de Pago - SaaSRRHH'
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];