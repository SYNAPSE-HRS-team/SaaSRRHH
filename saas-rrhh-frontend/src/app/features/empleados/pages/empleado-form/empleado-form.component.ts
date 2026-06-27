import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoRequest } from '../../../../core/models/empleado.model';

@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div class="header-left">
          <div class="breadcrumb">
            <a routerLink="/empleados">Empleados</a>
            <span class="separator">/</span>
            <span>Nuevo Empleado</span>
          </div>
          <h1>Registrar Nuevo Empleado</h1>
          <p class="subtitle">Ingresa los datos del nuevo empleado en el sistema</p>
        </div>
      </div>

      <form (ngSubmit)="onSubmit()" class="empleado-form">
        @if (error()) {
          <div class="error-banner">
            <span>⚠️</span>
            <span>{{ error() }}</span>
          </div>
        }

        @if (successMessage()) {
          <div class="success-banner">
            <span>✅</span>
            <span>{{ successMessage() }}</span>
          </div>
        }

        <div class="form-section">
          <h2 class="section-title">Información Personal</h2>
          <div class="form-grid">
            <div class="form-group">
              <label for="nombres">Nombres <span class="required">*</span></label>
              <input
                type="text" id="nombres" name="nombres"
                [(ngModel)]="formData.nombres" required
                placeholder="Nombres del empleado"
              />
            </div>
            <div class="form-group">
              <label for="apellidos">Apellidos <span class="required">*</span></label>
              <input
                type="text" id="apellidos" name="apellidos"
                [(ngModel)]="formData.apellidos" required
                placeholder="Apellidos del empleado"
              />
            </div>
            <div class="form-group">
              <label for="dni">DNI <span class="required">*</span></label>
              <input
                type="text" id="dni" name="dni"
                [(ngModel)]="formData.dni" required maxlength="8" minlength="8"
                placeholder="12345678"
              />
            </div>
            <div class="form-group">
              <label for="cargo">Cargo</label>
              <input
                type="text" id="cargo" name="cargo"
                [(ngModel)]="formData.cargo"
                placeholder="Ej: Desarrollador Senior"
              />
            </div>
          </div>
        </div>

        <div class="form-section">
          <h2 class="section-title">Información del Contrato</h2>
          <div class="form-grid">
            <div class="form-group">
              <label for="sueldoBase">Sueldo Base (S/)</label>
              <input
                type="number" id="sueldoBase" name="sueldoBase" step="0.01" min="0"
                [(ngModel)]="formData.sueldoBase"
                placeholder="0.00"
              />
            </div>
            <div class="form-group">
              <label for="fechaInicioContrato">Fecha Inicio Contrato</label>
              <input
                type="date" id="fechaInicioContrato" name="fechaInicioContrato"
                [(ngModel)]="formData.fechaInicioContrato"
              />
            </div>
            <div class="form-group">
              <label for="fechaFinContrato">Fecha Fin Contrato</label>
              <input
                type="date" id="fechaFinContrato" name="fechaFinContrato"
                [(ngModel)]="formData.fechaFinContrato"
              />
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input
                  type="checkbox" id="asignacionFamiliar" name="asignacionFamiliar"
                  [(ngModel)]="formData.asignacionFamiliar"
                />
                Asignación Familiar
              </label>
            </div>
          </div>
        </div>

        <div class="form-section">
          <h2 class="section-title">Estado</h2>
          <div class="form-grid">
            <div class="form-group">
              <label class="checkbox-label">
                <input
                  type="checkbox" id="activo" name="activo"
                  [(ngModel)]="formData.activo"
                />
                Empleado Activo
              </label>
              <p class="field-hint">Los empleados inactivos no aparecerán en las consultas por defecto</p>
            </div>
            <div class="form-group">
              <label for="fotoPerfilUrl">URL de Foto de Perfil</label>
              <input
                type="url" id="fotoPerfilUrl" name="fotoPerfilUrl"
                [(ngModel)]="formData.fotoPerfilUrl"
                placeholder="https://ejemplo.com/foto.jpg"
              />
            </div>
          </div>
        </div>

        <div class="form-actions">
          <a routerLink="/empleados" class="btn-cancel">Cancelar</a>
          <button type="submit" class="btn-primary" [disabled]="saving()">
            @if (saving()) {
              <span class="spinner"></span>
              Guardando...
            } @else {
              <span>💾</span>
              Guardar Empleado
            }
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-container { max-width: 900px; margin: 0 auto; }
    .page-header { margin-bottom: 2rem; }
    .header-left h1 { margin: 0; color: #1a1a2e; font-size: 1.75rem; }
    .subtitle { color: #666; margin: 0.25rem 0 0; }

    .breadcrumb { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.5rem; font-size: 0.85rem; }
    .breadcrumb a { color: #667eea; text-decoration: none; }
    .breadcrumb a:hover { text-decoration: underline; }
    .separator { color: #ccc; }

    .empleado-form {
      background: white; border-radius: 16px; padding: 2rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }

    .error-banner, .success-banner {
      display: flex; align-items: center; gap: 0.75rem;
      padding: 0.85rem 1rem; border-radius: 10px; margin-bottom: 1.5rem;
      font-size: 0.9rem;
    }
    .error-banner { background: #fef2f2; color: #dc2626; }
    .success-banner { background: #ecfdf5; color: #059669; }

    .form-section { margin-bottom: 2rem; padding-bottom: 2rem; border-bottom: 1px solid #f0f0f0; }
    .form-section:last-of-type { border-bottom: none; margin-bottom: 1.5rem; padding-bottom: 0; }

    .section-title {
      font-size: 1.1rem; color: #1a1a2e; margin: 0 0 1.25rem; font-weight: 600;
    }

    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; }

    .form-group { display: flex; flex-direction: column; gap: 0.4rem; }
    .form-group label { font-size: 0.85rem; font-weight: 600; color: #333; }
    .required { color: #dc2626; }

    .form-group input[type="text"],
    .form-group input[type="number"],
    .form-group input[type="date"],
    .form-group input[type="url"] {
      padding: 0.7rem 0.9rem;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      font-size: 0.9rem;
      transition: border-color 0.2s;
      outline: none;
    }
    .form-group input:focus {
      border-color: #667eea;
    }

    .checkbox-label {
      display: flex !important;
      flex-direction: row !important;
      align-items: center;
      gap: 0.5rem;
      cursor: pointer;
      padding: 0.7rem 0;
    }
    .checkbox-label input[type="checkbox"] {
      width: 18px; height: 18px; cursor: pointer;
    }

    .field-hint {
      font-size: 0.8rem; color: #888; margin: 0;
    }

    .form-actions {
      display: flex; gap: 1rem; justify-content: flex-end;
      padding-top: 1.5rem; border-top: 1px solid #f0f0f0;
    }

    .btn-primary {
      display: inline-flex; align-items: center; gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white; border: none; border-radius: 8px;
      font-size: 0.95rem; font-weight: 600; cursor: pointer;
      transition: all 0.2s;
    }
    .btn-primary:hover:not(:disabled) {
      transform: translateY(-1px); box-shadow: 0 4px 15px rgba(102,126,234,0.4);
    }
    .btn-primary:disabled { opacity: 0.7; cursor: not-allowed; }

    .btn-cancel {
      display: inline-flex; align-items: center; gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      background: white; color: #555; border: 2px solid #e0e0e0; border-radius: 8px;
      font-size: 0.95rem; font-weight: 500; cursor: pointer; text-decoration: none;
      transition: all 0.2s;
    }
    .btn-cancel:hover { border-color: #ccc; background: #fafafa; }

    .spinner {
      width: 16px; height: 16px;
      border: 2px solid rgba(255,255,255,0.3); border-top-color: white;
      border-radius: 50%; animation: spin 0.6s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class EmpleadoFormComponent {
  formData: EmpleadoRequest = {
    nombres: '',
    apellidos: '',
    dni: '',
    cargo: '',
    sueldoBase: undefined,
    fechaInicioContrato: '',
    fechaFinContrato: '',
    asignacionFamiliar: false,
    activo: true,
    fotoPerfilUrl: ''
  };

  saving = signal(false);
  error = signal('');
  successMessage = signal('');

  constructor(
    private empleadoService: EmpleadoService,
    private router: Router
  ) {}

  onSubmit(): void {
    // Validaciones
    if (!this.formData.nombres?.trim() || !this.formData.apellidos?.trim()) {
      this.error.set('Nombres y apellidos son obligatorios');
      return;
    }

    if (!this.formData.dni?.trim() || this.formData.dni.length !== 8) {
      this.error.set('El DNI debe tener exactamente 8 dígitos');
      return;
    }

    this.saving.set(true);
    this.error.set('');
    this.successMessage.set('');

    this.empleadoService.create(this.formData).subscribe({
      next: (response) => {
        this.saving.set(false);
        this.successMessage.set(`Empleado "${response.nombres} ${response.apellidos}" creado exitosamente`);
        setTimeout(() => this.router.navigate(['/empleados']), 1500);
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message || 'Error al crear el empleado');
      }
    });
  }
}
