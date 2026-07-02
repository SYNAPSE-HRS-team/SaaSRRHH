import { Routes } from '@angular/router';
import { ReporteIncidenteFormComponent } from './pages/reporte-incidente-form/reporte-incidente-form.component';
import { ReporteIncidenteListComponent } from './pages/reporte-incidente-list/reporte-incidente-list.component';

export const REPORTE_INCIDENTE_ROUTES: Routes = [
  {
    path: '',
    component: ReporteIncidenteListComponent,
    data: { title: 'Reportes de Incidentes' },
  },
  {
    path: 'nuevo',
    component: ReporteIncidenteFormComponent,
    data: { title: 'Nuevo Reporte de Incidente' },
  },
  {
    path: 'editar/:id',
    component: ReporteIncidenteFormComponent,
    data: { title: 'Editar Reporte de Incidente' },
  },
  {
    path: 'ver/:id',
    component: ReporteIncidenteFormComponent,
    data: { title: 'Ver Reporte de Incidente' },
  },
];
