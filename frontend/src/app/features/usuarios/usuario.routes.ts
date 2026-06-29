import { Routes } from '@angular/router';

export const usuarioRoutes: Routes = [
  {
    path: '',
    loadComponent: () => 
      import('./usuarios.component')
        .then(m => m.UsuariosComponent)
  }
];