import { Routes } from '@angular/router';

export const usuarioRoutes: Routes = [
  {
    path: '',
    loadComponent: () => 
      import('./pages/usuario-list/usuario-list.component')
        .then(m => m.UsuarioListComponent)
  }
];