import { Routes } from '@angular/router';
import { EncuestaListComponent } from './pages/encuesta-list/encuesta-list.component';
import { EncuestaFormComponent } from './pages/encuesta-form/encuesta-form.component';
import { MetricasBurnoutListComponent } from './pages/metricas-burnout-list/metricas-burnout-list.component';

export const BIENESTAR_ROUTES: Routes = [
    {
        path: '',
        component: EncuestaListComponent,
        data: { title: 'Bienestar' }
    },
    {
        path: 'encuestas',
        component: EncuestaListComponent,
        data: { title: 'Encuestas' }
    },
    {
        path: 'encuestas/nuevo',
        component: EncuestaFormComponent,
        data: { title: 'Nueva Encuesta' }
    },
    {
        path: 'encuestas/:id',
        component: EncuestaFormComponent,
        data: { title: 'Ver Encuesta' }
    },
    {
        path: 'encuestas/editar/:id',
        component: EncuestaFormComponent,
        data: { title: 'Editar Encuesta' }
    },
    {
        path: 'metricas-burnout',
        component: MetricasBurnoutListComponent,
        data: { title: 'Métricas de Burnout' }
    }
];