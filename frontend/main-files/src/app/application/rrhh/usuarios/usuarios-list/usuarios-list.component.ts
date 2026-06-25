import { Component, OnInit } from '@angular/core';
import { UsuariosService } from '../usuarios.service';

@Component({
  selector: 'app-usuarios-list',
  templateUrl: './usuarios-list.component.html',
})
export class UsuariosListComponent implements OnInit {
  usuarios: any[] = [];

  constructor(private service: UsuariosService) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.service.listar().subscribe((data) => {
      console.log('DATA API:', data);
      this.usuarios = data;
    });
  }
}
