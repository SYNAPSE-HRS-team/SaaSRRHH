import { Routes } from '@angular/router';

export const tareaRoutes: Routes = [
  {
    path: '',
    loadComponent: () => 
      import('./pages/tarea-list/tarea-list.component')
        .then(m => m.TareaListComponent)
  }
];