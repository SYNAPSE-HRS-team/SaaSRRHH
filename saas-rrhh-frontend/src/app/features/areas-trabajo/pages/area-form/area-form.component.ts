import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AreaTrabajoService } from '../../../../core/services/area-trabajo.service';
import { AreaTrabajo } from '../../../../core/models/area-trabajo.model';

@Component({
  selector: 'app-area-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div class="header-left">
          <div class="breadcrumb">
            <a routerLink="/areas-trabajo">Áreas de Trabajo</a>
            <span class="separator">/</span>
            <span>{{ isEditing ? 'Editar' : 'Nueva' }} Área</span>
          </div>
          <h1>{{ isEditing ? 'Editar Área' : 'Nueva Área de Trabajo' }}</h1>
          <p class="subtitle">{{ isEditing ? 'Modifica los datos del área' : 'Registra una nueva área de trabajo' }}</p>
        </div>
      </div>

      <form (ngSubmit)="onSubmit()" class="area-form">
        @if (error()) {
          <div class="error-banner">⚠️ {{ error() }}</div>
        }

        @if (successMessage()) {
          <div class="success-banner">✅ {{ successMessage() }}</div>
        }

        <div class="form-section">
          <div class="form-grid">
            <div class="form-group">
              <label for="nombre">Nombre del Área <span class="required">*</span></label>
              <input type="text" id="nombre" name="nombre"
                [(ngModel)]="formData.nombre" required
                placeholder="Ej: Campo Norte, Invernadero Sur"
              />
            </div>
            <div class="form-group">
              <label for="cultivoTipo">Tipo de Cultivo</label>
              <input type="text" id="cultivoTipo" name="cultivoTipo"
                [(ngModel)]="formData.cultivoTipo"
                placeholder="Ej: Arándanos, Palta, Espárragos"
              />
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" id="activo" name="activo"
                  [(ngModel)]="formData.activo"
                />
                Área Activa
              </label>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <a routerLink="/areas-trabajo" class="btn-cancel">Cancelar</a>
          <button type="submit" class="btn-primary" [disabled]="saving()">
            @if (saving()) {
              <span class="spinner"></span> Guardando...
            } @else {
              <span>💾</span> {{ isEditing ? 'Actualizar' : 'Guardar' }} Área
            }
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-container { max-width: 700px; margin: 0 auto; }
    .page-header { margin-bottom: 2rem; }
    .header-left h1 { margin: 0; color: #1a1a2e; font-size: 1.75rem; }
    .subtitle { color: #666; margin: 0.25rem 0 0; }
    .breadcrumb { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.5rem; font-size: 0.85rem; }
    .breadcrumb a { color: #667eea; text-decoration: none; }
    .breadcrumb a:hover { text-decoration: underline; }
    .separator { color: #ccc; }

    .area-form { background: white; border-radius: 16px; padding: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
    .error-banner, .success-banner { display: flex; align-items: center; gap: 0.75rem; padding: 0.85rem 1rem; border-radius: 10px; margin-bottom: 1.5rem; font-size: 0.9rem; }
    .error-banner { background: #fef2f2; color: #dc2626; }
    .success-banner { background: #ecfdf5; color: #059669; }

    .form-section { margin-bottom: 2rem; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; }
    .form-group { display: flex; flex-direction: column; gap: 0.4rem; }
    .form-group label { font-size: 0.85rem; font-weight: 600; color: #333; }
    .required { color: #dc2626; }
    .form-group input[type="text"] { padding: 0.7rem 0.9rem; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 0.9rem; transition: border-color 0.2s; outline: none; }
    .form-group input:focus { border-color: #667eea; }
    .checkbox-label { display: flex !important; flex-direction: row !important; align-items: center; gap: 0.5rem; cursor: pointer; padding: 0.7rem 0; }
    .checkbox-label input[type="checkbox"] { width: 18px; height: 18px; cursor: pointer; }

    .form-actions { display: flex; gap: 1rem; justify-content: flex-end; padding-top: 1.5rem; border-top: 1px solid #f0f0f0; }
    .btn-primary { display: inline-flex; align-items: center; gap: 0.5rem; padding: 0.75rem 1.5rem; background: linear-gradient(135deg, #667eea, #764ba2); color: white; border: none; border-radius: 8px; font-size: 0.95rem; font-weight: 600; cursor: pointer; transition: all 0.2s; }
    .btn-primary:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 4px 15px rgba(102,126,234,0.4); }
    .btn-primary:disabled { opacity: 0.7; cursor: not-allowed; }
    .btn-cancel { display: inline-flex; align-items: center; gap: 0.5rem; padding: 0.75rem 1.5rem; background: white; color: #555; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 0.95rem; font-weight: 500; cursor: pointer; text-decoration: none; transition: all 0.2s; }
    .btn-cancel:hover { border-color: #ccc; background: #fafafa; }
    .spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class AreaFormComponent implements OnInit {
  formData: AreaTrabajo = {
    nombre: '',
    cultivoTipo: '',
    activo: true
  };

  isEditing = false;
  areaId: number | null = null;
  saving = signal(false);
  error = signal('');
  successMessage = signal('');

  constructor(
    private areaService: AreaTrabajoService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditing = true;
      this.areaId = +id;
      this.loadArea(this.areaId);
    }
  }

  loadArea(id: number): void {
    this.areaService.getById(id).subscribe({
      next: (area) => {
        this.formData = { ...area };
      },
      error: () => this.error.set('Error al cargar el área')
    });
  }

  onSubmit(): void {
    if (!this.formData.nombre?.trim()) {
      this.error.set('El nombre del área es obligatorio');
      return;
    }

    this.saving.set(true);
    this.error.set('');

    const request = this.isEditing && this.areaId
      ? this.areaService.update(this.areaId, this.formData)
      : this.areaService.create(this.formData);

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.successMessage.set(`Área "${this.formData.nombre}" ${this.isEditing ? 'actualizada' : 'creada'} exitosamente`);
        setTimeout(() => this.router.navigate(['/areas-trabajo']), 1500);
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message || 'Error al guardar el área');
      }
    });
  }
}
