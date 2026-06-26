import { Component, OnInit } from '@angular/core';
import { UsuariosService } from '../usuarios.service';

@Component({
  selector: 'app-usuarios-list',
  templateUrl: './usuarios-list.component.html',
})
export class UsuariosListComponent implements OnInit {
  // ✅ Cambia de UsuarioResponse[] a any[]
  usuarios: any[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private service: UsuariosService) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.loading = true;
    this.error = '';

    this.service.listar().subscribe({
      next: (data) => {
        console.log('✅ DATA API:', data);
        this.usuarios = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Error:', error);
        this.error = 'Error al cargar los usuarios';
        this.loading = false;
      },
    });
  }

  eliminarUsuario(id: number): void {
    if (confirm('¿Estás seguro de eliminar este usuario?')) {
      this.service.eliminar(id).subscribe({
        next: () => {
          alert('✅ Usuario eliminado');
          this.cargarUsuarios();
        },
        error: (error) => {
          console.error('❌ Error eliminando:', error);
          alert('❌ Error al eliminar el usuario');
        },
      });
    }
  }
}
