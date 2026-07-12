import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';
import { RoleGuard } from '../../core/guards/role.guard';
import { EncuestaListComponent } from './pages/encuesta-list/encuesta-list.component';
import { EncuestaFormComponent } from './pages/encuesta-form/encuesta-form.component';
import { MetricasBurnoutListComponent } from './pages/metricas-burnout-list/metricas-burnout-list.component';

export const BIENESTAR_ROUTES: Routes = [
  {
    path: '',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SUPERVISOR'] },
    component: EncuestaListComponent,
    title: 'Bienestar - SaaSRRHH'
  },
  {
    path: 'encuestas',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SUPERVISOR'] },
    component: EncuestaListComponent,
    title: 'Encuestas - SaaSRRHH'
  },
  {
    path: 'encuestas/nuevo',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SUPERVISOR'] },
    component: EncuestaFormComponent,
    title: 'Nueva Encuesta - SaaSRRHH'
  },
  {
    path: 'encuestas/:id',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SUPERVISOR'] },
    component: EncuestaFormComponent,
    title: 'Ver Encuesta - SaaSRRHH'
  },
  {
    path: 'encuestas/editar/:id',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SUPERVISOR'] },
    component: EncuestaFormComponent,
    title: 'Editar Encuesta - SaaSRRHH'
  },
  {
    path: 'metricas-burnout',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    component: MetricasBurnoutListComponent,
    title: 'Métricas de Burnout - SaaSRRHH'
  }
];