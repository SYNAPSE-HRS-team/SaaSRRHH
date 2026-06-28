import { Component, OnInit, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsuarioService } from '../../../../core/services/usuario.service';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="modal-backdrop">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editMode ? 'Editar Usuario' : 'Nuevo Usuario' }}</h3>
          <button (click)="cerrar()" class="close-btn">&times;</button>
        </div>

        <form [formGroup]="usuarioForm" (ngSubmit)="onSubmit()" class="modal-form">
          <div class="form-group">
            <label>Correo Electrónico</label>
            <input type="email" formControlName="email" />
            <div *ngIf="usuarioForm.get('email')?.touched && usuarioForm.get('email')?.invalid" class="error-msg">
              <small *ngIf="usuarioForm.get('email')?.errors?.['required']">El correo es obligatorio.</small>
              <small *ngIf="usuarioForm.get('email')?.errors?.['email']">Formato de correo inválido.</small>
            </div>
          </div>

          <div class="form-group">
            <label>Contraseña</label>
            <input type="password" formControlName="password" placeholder="••••••••" />
            <div *ngIf="usuarioForm.get('password')?.touched && usuarioForm.get('password')?.invalid" class="error-msg">
              <small>La contraseña es obligatoria para nuevos usuarios.</small>
            </div>
            <small *ngIf="editMode" style="color: #666; display: block; margin-top: 0.25rem;">
              * Deja este campo en blanco si no deseas cambiar la contraseña actual.
            </small>
          </div>

          <div class="form-group">
            <label>Rol de Usuario</label>
            <select formControlName="rolId">
              <option [value]="1">ADMIN</option>
              <option [value]="2">SUPERVISOR</option>
              <option [value]="3">TRABAJADOR</option>
            </select>
          </div>

          <div class="form-checkbox">
            <input type="checkbox" id="activo" formControlName="activo" />
            <label for="activo">Cuenta activa / Habilitada</label>
          </div>

          <div class="form-actions">
            <button type="button" (click)="cerrar()" class="btn-cancel">Cancelar</button>
            <button type="submit" [disabled]="usuarioForm.invalid" class="btn-submit">
              Guardar
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [
    `
      .modal-backdrop { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1100; }
      .modal-content { background: white; border-radius: 8px; width: 100%; max-width: 400px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); }
      .modal-header { padding: 1rem; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; h3 { margin: 0; font-size: 1.1rem; } .close-btn { background: none; border: none; font-size: 1.25rem; cursor: pointer; } }
      .modal-form { padding: 1rem;
        .form-group { margin-bottom: 1rem; label { display: block; font-size: 0.75rem; font-weight: 600; margin-bottom: 0.25rem; color: #555; } input, select { width: 100%; padding: 0.5rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; } }
        .form-checkbox { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; margin-bottom: 1rem; }
        .error-msg { color: #e53e3e; margin-top: 0.25rem; display: block; }
      }
      .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #eee;
        button { padding: 0.5rem 1rem; border-radius: 4px; border: none; cursor: pointer; font-size: 0.85rem; }
        .btn-cancel { background: #f5f5f5; color: #333; }
        .btn-submit { background: #667eea; color: white; &:disabled { opacity: 0.5; } }
      }
    `,
  ],
})
export class UsuarioFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private usuarioService = inject(UsuarioService);

  // Recibe la información de control desde el componente padre
  @Input() usuarioData: any = null;
  @Input() editMode = false;

  // Notifica eventos hacia el componente padre
  @Output() onCerrar = new EventEmitter<void>();
  @Output() onExito = new EventEmitter<void>();

  usuarioForm!: FormGroup;

  ngOnInit(): void {
    this.initForm();
    this.configurarFormulario();
  }

  initForm(): void {
    this.usuarioForm = this.fb.group({
      id: [null],
      email: ['', [Validators.required, Validators.email]],
      password: [''],
      rolId: [3, Validators.required],
      activo: [true],
    });
  }

  configurarFormulario(): void {
    if (this.editMode && this.usuarioData) {
      // Modo Edición: Contraseña opcional
      this.usuarioForm.get('password')?.clearValidators();
      this.usuarioForm.get('password')?.updateValueAndValidity();

      this.usuarioForm.patchValue({
        id: this.usuarioData.id,
        email: this.usuarioData.email,
        activo: this.usuarioData.activo ?? true,
        rolId: this.usuarioData.rol?.id || this.usuarioData.rolId || 3,
        password: '', 
      });
    } else {
      // Modo Creación: Contraseña obligatoria
      this.usuarioForm.get('password')?.setValidators([Validators.required]);
      this.usuarioForm.get('password')?.updateValueAndValidity();
    }
  }

  cerrar(): void {
    this.onCerrar.emit();
  }

  onSubmit(): void {
    if (this.usuarioForm.invalid) return;

    const formValue = this.usuarioForm.value;
    const data: any = {
      email: formValue.email,
      activo: formValue.activo,
      rolId: Number(formValue.rolId),
      password: formValue.password || null,
    };

    if (this.editMode) {
      const id = formValue.id;
      this.usuarioService.actualizar(id, data).subscribe({
        next: () => this.onExito.emit(),
        error: (err) => alert('Error al actualizar el usuario en el backend.'),
      });
    } else {
      this.usuarioService.guardar(data).subscribe({
        next: () => this.onExito.emit(),
        error: (err) => alert('Error al crear el nuevo usuario.'),
      });
    }
  }
}