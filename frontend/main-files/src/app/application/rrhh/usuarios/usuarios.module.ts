import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { UsuarioFormComponent } from './usuario-form/usuario-form.component';
import { UsuariosListComponent } from './usuarios-list/usuarios-list.component';
import { UsuariosRoutingModule } from './usuarios-routing.module';

@NgModule({
  declarations: [UsuariosListComponent, UsuarioFormComponent],
  imports: [CommonModule, FormsModule, UsuariosRoutingModule],
})
export class UsuariosModule {}
