import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsuarioFormComponent } from './usuario-form/usuario-form.component'; // ← Importar
import { UsuariosListComponent } from './usuarios-list/usuarios-list.component';

const routes: Routes = [
  { path: '', component: UsuariosListComponent },
  { path: 'nuevo', component: UsuarioFormComponent }, // ← Agregar esta línea
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UsuariosRoutingModule {}
