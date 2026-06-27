import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';
import { UsuarioResponse, UsuarioRequest } from '../../core/models/usuario.model';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="usuarios-container">
      <div class="page-header">
        <div>
          <h1>Gestión de Usuarios</h1>
          <p class="subtitle">Módulo de administración - SaaS RRHH</p>
        </div>
        <button (click)="abrirModal()" class="btn-primary">
          + Nuevo Usuario
        </button>
      </div>

      <div class="table-responsive">
        <table class="custom-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Usuario</th>
              <th>Correo</th>
              <th>Rol</th>
              <th>Estado</th>
              <th class="text-right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let usuario of usuarios">
              <td>#{{ usuario.idUsuario || usuario.id }}</td>
              <td class="font-medium">{{ usuario.nombreUsuario || usuario.username }}</td>
              <td>{{ usuario.correo || usuario.email }}</td>
              <td>
                <span class="badge badge-blue">
                  {{ usuario.rol?.nombreRol || usuario.role || 'ADMIN' }}
                </span>
              </td>
              <td>
                <span class="badge" [ngClass]="(usuario.estado ?? true) ? 'badge-green' : 'badge-red'">
                  {{ (usuario.estado ?? true) ? 'Activo' : 'Inactivo' }}
                </span>
              </td>
              <td class="text-right actions-cell">
                <button (click)="editarUsuario(usuario)" class="btn-edit">Editar</button>
                <button (click)="eliminarUsuario(usuario.idUsuario || usuario.id)" class="btn-delete">Eliminar</button>
              </td>
            </tr>
            <tr *ngIf="usuarios.length === 0">
              <td colspan="6" class="no-data">No hay usuarios registrados o no se pudo conectar con el servidor.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="isModalOpen" class="modal-backdrop">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ editMode ? 'Editar Usuario' : 'Nuevo Usuario' }}</h3>
            <button (click)="cerrarModal()" class="close-btn">&times;</button>
          </div>
          
          <form [formGroup]="usuarioForm" (ngSubmit)="onSubmit()" class="modal-form">
            <div class="form-group">
              <label>Nombre de Usuario</label>
              <input type="text" formControlName="nombreUsuario">
            </div>

            <div class="form-group">
              <label>Correo Electrónico</label>
              <input type="email" formControlName="correo">
            </div>

            <div class="form-group" *ngIf="!editMode">
              <label>Contraseña</label>
              <input type="password" formControlName="contrasena">
            </div>

            <div class="form-group">
              <label>Rol de Usuario</label>
              <select formControlName="idRol">
                <option [value]="1">ADMIN</option>
                <option [value]="2">SUPERVISOR</option>
                <option [value]="3">TRABAJADOR</option>
              </select>
            </div>

            <div class="form-checkbox">
              <input type="checkbox" id="estado" formControlName="estado">
              <label for="estado">Cuenta activa / Habilitada</label>
            </div>

            <div class="form-actions">
              <button type="button" (click)="cerrarModal()" class="btn-cancel">Cancelar</button>
              <button type="submit" [disabled]="usuarioForm.invalid" class="btn-submit">
                Guardar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    /* 🎨 ESTILOS PUROS SASS COMPATIBLES CON TU LAYOUT */
    .usuarios-container {
      padding: 1.5rem;
      max-width: 1200px;
      margin: 0 auto;
      font-family: inherit;
    }
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
      h1 { margin: 0; font-size: 1.5rem; color: #1a1a2e; font-weight: 700; }
      .subtitle { margin: 0.25rem 0 0; font-size: 0.85rem; color: #666; }
    }
    .btn-primary {
      background: #667eea;
      color: white;
      padding: 0.5rem 1rem;
      border-radius: 6px;
      border: none;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.2s;
      &:hover { background: #5a6fd6; }
    }
    .table-responsive {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.06);
      border: 1px solid #e1e4e8;
      overflow-x: auto;
    }
    .custom-table {
      width: 100%;
      border-collapse: collapse;
      text-align: left;
      font-size: 0.9rem;
      th { background: #f8f9fa; padding: 0.75rem 1rem; color: #555; font-weight: 600; border-bottom: 2px solid #e1e4e8; }
      td { padding: 0.75rem 1rem; border-bottom: 1px solid #e1e4e8; color: #333; }
      .font-medium { font-weight: 500; }
      .text-right { text-align: right; }
      .no-data { text-align: center; padding: 2rem; color: #999; }
    }
    .badge {
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 600;
      display: inline-block;
      &-blue { background: #e0e7ff; color: #4338ca; }
      &-green { background: #dcfce7; color: #15803d; }
      &-red { background: #fee2e2; color: #b91c1c; }
    }
    .actions-cell button {
      background: none; border: none; font-weight: 500; cursor: pointer; margin-left: 0.75rem;
      &.btn-edit { color: #667eea; &:hover { text-decoration: underline; } }
      &.btn-delete { color: #e53e3e; &:hover { text-decoration: underline; } }
    }
    /* Estilos del Modal */
    .modal-backdrop {
      position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1100;
    }
    .modal-content {
      background: white; border-radius: 8px; width: 100%; max-width: 400px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    }
    .modal-header {
      padding: 1rem; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center;
      h3 { margin: 0; font-size: 1.1rem; }
      .close-btn { background: none; border: none; font-size: 1.25rem; cursor: pointer; }
    }
    .modal-form {
      padding: 1rem;
      .form-group {
        margin-bottom: 1rem;
        label { display: block; font-size: 0.75rem; font-weight: 600; margin-bottom: 0.25rem; color: #555; }
        input, select { width: 100%; padding: 0.5rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
      }
      .form-checkbox { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; }
    }
    .form-actions {
      display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #eee;
      button { padding: 0.5rem 1rem; border-radius: 4px; border: none; cursor: pointer; font-size: 0.85rem; }
      .btn-cancel { background: #f5f5f5; color: #333; }
      .btn-submit { background: #667eea; color: white; &:disabled { opacity: 0.5; } }
    }
  `]
})
export class UsuariosComponent implements OnInit {
  private usuarioService = inject(UsuarioService);
  private fb = inject(FormBuilder);

  usuarios: any[] = []; // Cambiado a any[] temporalmente para dar flexibilidad al mapeo dinámico
  usuarioForm!: FormGroup;
  isModalOpen = false;
  editMode = false;

  ngOnInit(): void {
    this.cargarUsuarios();
    this.initForm();
  }

  initForm(): void {
    this.usuarioForm = this.fb.group({
      idUsuario: [null],
      nombreUsuario: ['', Validators.required],
      correo: ['', [Validators.required, Validators.email]],
      contrasena: [''],
      idRol: [3, Validators.required],
      estado: [true]
    });
  }

  cargarUsuarios(): void {
    this.usuarioService.listar().subscribe({
      next: (data) => {
        console.log("DATOS FINALES RECIBIDOS:", data);
        this.usuarios = data;
      },
      error: (err) => console.error('Error al cargar la lista de usuarios', err)
    });
  }

  abrirModal(): void {
    this.editMode = false;
    this.usuarioForm.reset({ idRol: 3, estado: true });
    this.usuarioForm.get('contrasena')?.setValidators([Validators.required]);
    this.usuarioForm.get('contrasena')?.updateValueAndValidity();
    this.isModalOpen = true;
  }

  editarUsuario(usuario: any): void {
    this.editMode = true;
    this.usuarioForm.get('contrasena')?.clearValidators();
    this.usuarioForm.get('contrasena')?.updateValueAndValidity();
    
    this.usuarioForm.patchValue({
      idUsuario: usuario.idUsuario || usuario.id,
      nombreUsuario: usuario.nombreUsuario || usuario.username,
      correo: usuario.correo || usuario.email,
      estado: usuario.estado ?? true,
      idRol: usuario.rol?.idRol || usuario.idRol || 3
    });
    this.isModalOpen = true;
  }

  cerrarModal(): void {
    this.isModalOpen = false;
  }

  onSubmit(): void {
    if (this.usuarioForm.invalid) return;

    const formValue = this.usuarioForm.value;
    const data: UsuarioRequest = {
      nombreUsuario: formValue.nombreUsuario,
      correo: formValue.correo,
      contrasena: formValue.contrasena,
      estado: formValue.estado,
      idRol: Number(formValue.idRol)
    };

    if (this.editMode) {
      (data as any).idUsuario = formValue.idUsuario;
    }
    
    this.usuarioService.guardar(data).subscribe({
      next: () => {
        this.cargarUsuarios();
        this.cerrarModal();
      },
      error: (err) => alert('Error al guardar el usuario en el servidor backend.')
    });
  }

  eliminarUsuario(id: number): void {
    if (!id) return;
    if (confirm('¿Seguro que deseas dar de baja o eliminar este usuario?')) {
      this.usuarioService.eliminar(id).subscribe({
        next: () => this.cargarUsuarios(),
        error: (err) => console.error(err)
      });
    }
  }
}