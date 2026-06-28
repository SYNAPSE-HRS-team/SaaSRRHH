import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../../../core/services/usuario.service';
import { UsuarioFormComponent } from '../usuario-form/usuario-form.component';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [CommonModule, UsuarioFormComponent],
  template: `
    <div class="usuarios-container">
      <div class="page-header">
        <div>
          <h1>Gestión de Usuarios</h1>
          <p class="subtitle">Módulo de administración - SaaS RRHH</p>
        </div>
        <button (click)="abrirModal()" class="btn-primary">+ Nuevo Usuario</button>
      </div>

      <div class="table-responsive">
        <table class="custom-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Correo Electrónico</th>
              <th>Rol</th>
              <th>Estado</th>
              <th class="text-right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let usuario of usuarios">
              <td>#{{ usuario.id }}</td>
              <td class="font-medium">{{ usuario.email }}</td>
              <td>
                <span class="badge badge-blue">
                  {{ usuario.rolNombre || 'TRABAJADOR' }}
                </span>
              </td>
              <td>
                <span class="badge" [ngClass]="usuario.activo ? 'badge-green' : 'badge-red'">
                  {{ usuario.activo ? 'Activo' : 'Inactivo' }}
                </span>
              </td>
              <td class="text-right actions-cell">
                <button (click)="editarUsuario(usuario)" class="btn-edit">Editar</button>
                <button (click)="eliminarUsuario(usuario.id)" class="btn-delete">Eliminar</button>
              </td>
            </tr>
            <tr *ngIf="usuarios.length === 0">
              <td colspan="5" class="no-data">
                No hay usuarios registrados o no se pudo conectar con el servidor.
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <app-usuario-form
        *ngIf="isModalOpen"
        [usuarioData]="usuarioSeleccionado"
        [editMode]="editMode"
        (onCerrar)="cerrarModal()"
        (onExito)="alGuardarExitoso()"
      ></app-usuario-form>
    </div>
  `,
  styles: [
    `
      .usuarios-container { padding: 1.5rem; max-width: 1200px; margin: 0 auto; font-family: inherit; }
      .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;
        h1 { margin: 0; font-size: 1.5rem; color: #1a1a2e; font-weight: 700; }
        .subtitle { margin: 0.25rem 0 0; font-size: 0.85rem; color: #666; }
      }
      .btn-primary { background: #667eea; color: white; padding: 0.5rem 1rem; border-radius: 6px; border: none; font-weight: 500; cursor: pointer; transition: background 0.2s; &:hover { background: #5a6fd6; } }
      .table-responsive { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); border: 1px solid #e1e4e8; overflow-x: auto; }
      .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem;
        th { background: #f8f9fa; padding: 0.75rem 1rem; color: #555; font-weight: 600; border-bottom: 2px solid #e1e4e8; }
        td { padding: 0.75rem 1rem; border-bottom: 1px solid #e1e4e8; color: #333; }
        .font-medium { font-weight: 500; }
        .text-right { text-align: right; }
        .no-data { text-align: center; padding: 2rem; color: #999; }
      }
      .badge { padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; display: inline-block;
        &-blue { background: #e0e7ff; color: #4338ca; }
        &-green { background: #dcfce7; color: #15803d; }
        &-red { background: #fee2e2; color: #b91c1c; }
      }
      .actions-cell button { background: none; border: none; font-weight: 500; cursor: pointer; margin-left: 0.75rem;
        &.btn-edit { color: #667eea; &:hover { text-decoration: underline; } }
        &.btn-delete { color: #e53e3e; &:hover { text-decoration: underline; } }
      }
    `,
  ],
})
export class UsuarioListComponent implements OnInit {
  private usuarioService = inject(UsuarioService);

  usuarios: any[] = [];
  isModalOpen = false;
  editMode = false;
  usuarioSeleccionado: any = null;

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.usuarioService.listar().subscribe({
      next: (data) => this.usuarios = data,
      error: (err) => console.error('Error al cargar la lista de usuarios', err),
    });
  }

  abrirModal(): void {
    this.editMode = false;
    this.usuarioSeleccionado = null;
    this.isModalOpen = true;
  }

  editarUsuario(usuario: any): void {
    this.editMode = true;
    this.usuarioSeleccionado = usuario;
    this.isModalOpen = true;
  }

  cerrarModal(): void {
    this.isModalOpen = false;
    this.usuarioSeleccionado = null;
  }

  alGuardarExitoso(): void {
    this.cargarUsuarios();
    this.cerrarModal();
  }

  eliminarUsuario(id: number): void {
    if (!id) return;
    if (confirm('¿Seguro que deseas dar de baja o eliminar este usuario?')) {
      this.usuarioService.eliminar(id).subscribe({
        next: () => this.cargarUsuarios(),
        error: (err) => console.error(err),
      });
    }
  }
}