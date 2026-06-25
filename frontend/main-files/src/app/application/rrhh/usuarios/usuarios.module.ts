import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsuariosRoutingModule } from './usuarios-routing.module';
import { UsuariosListComponent } from './usuarios-list/usuarios-list.component';


@NgModule({
  declarations: [
    UsuariosListComponent
  ],
  imports: [
    CommonModule,
    UsuariosRoutingModule
  ]
})
export class UsuariosModule { }
