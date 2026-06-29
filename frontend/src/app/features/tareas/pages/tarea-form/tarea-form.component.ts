import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  EstadoTarea,
  FuncionTarea,
  TareaAsignadaResponse,
} from '../../../../core/models/tarea-asignada.model';
import { AreaTrabajoService } from '../../../../core/services/area-trabajo.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';

@Component({
  selector: 'app-tarea-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  styleUrls: ['./tarea-form.component.scss'],
  template: `
    <div class="modal-overlay">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ editMode ? 'Editar Tarea' : 'Asignar Nueva Tarea' }}</h3>
          <button (click)="cerrar()" class="btn-close">&times;</button>
        </div>

        <form [formGroup]="tareaForm" (ngSubmit)="onSubmit()" class="modal-body">
          <!-- Empleado (solo trabajadores) -->
          <div class="input-block">
            <label>Empleado <span class="required-mark">*</span></label>
            <select
              formControlName="empleadoId"
              [class.has-error]="
                tareaForm.get('empleadoId')?.touched && tareaForm.get('empleadoId')?.invalid
              "
            >
              <option [ngValue]="null" disabled>Selecciona un empleado...</option>
              <option *ngFor="let emp of empleados" [value]="emp.id">
                {{ emp.nombres }} {{ emp.apellidos }} - {{ emp.dni }}
              </option>
            </select>
            <div
              *ngIf="tareaForm.get('empleadoId')?.touched && tareaForm.get('empleadoId')?.invalid"
              class="error-text"
            >
              ⚠️ El empleado es obligatorio
            </div>
          </div>

          <!-- Supervisor (solo supervisores) -->
          <div class="input-block">
            <label>Supervisor <span class="required-mark">*</span></label>
            <select
              formControlName="supervisorId"
              [class.has-error]="
                tareaForm.get('supervisorId')?.touched && tareaForm.get('supervisorId')?.invalid
              "
            >
              <option [ngValue]="null" disabled>Selecciona un supervisor...</option>
              <option *ngFor="let sup of supervisores" [value]="sup.id">
                {{ sup.nombres }} {{ sup.apellidos }}
              </option>
            </select>
            <div
              *ngIf="
                tareaForm.get('supervisorId')?.touched && tareaForm.get('supervisorId')?.invalid
              "
              class="error-text"
            >
              ⚠️ El supervisor es obligatorio
            </div>
          </div>

          <!-- Área -->
          <div class="input-block">
            <label>Área <span class="required-mark">*</span></label>
            <select
              formControlName="areaId"
              [class.has-error]="
                tareaForm.get('areaId')?.touched && tareaForm.get('areaId')?.invalid
              "
            >
              <option [ngValue]="null" disabled>Selecciona un área...</option>
              <option *ngFor="let area of areas" [value]="area.id">{{ area.nombre }}</option>
            </select>
            <div
              *ngIf="tareaForm.get('areaId')?.touched && tareaForm.get('areaId')?.invalid"
              class="error-text"
            >
              ⚠️ El área es obligatoria
            </div>
          </div>

          <!-- Función -->
          <div class="input-block">
            <label>Función <span class="required-mark">*</span></label>
            <select
              formControlName="funcion"
              [class.has-error]="
                tareaForm.get('funcion')?.touched && tareaForm.get('funcion')?.invalid
              "
            >
              <option value="" disabled>Selecciona una función...</option>
              <option *ngFor="let func of funciones" [value]="func">{{ func }}</option>
            </select>
            <div
              *ngIf="tareaForm.get('funcion')?.touched && tareaForm.get('funcion')?.invalid"
              class="error-text"
            >
              ⚠️ La función es obligatoria
            </div>
          </div>

          <!-- Fecha -->
          <div class="input-block">
            <label>Fecha <span class="required-mark">*</span></label>
            <input
              type="date"
              formControlName="fecha"
              [class.has-error]="tareaForm.get('fecha')?.touched && tareaForm.get('fecha')?.invalid"
            />
            <div
              *ngIf="
                tareaForm.get('fecha')?.touched && tareaForm.get('fecha')?.errors?.['required']
              "
              class="error-text"
            >
              ⚠️ La fecha es obligatoria
            </div>
            <div
              *ngIf="tareaForm.get('fecha')?.touched && tareaForm.get('fecha')?.invalid"
              class="error-text"
            >
              ⚠️ La fecha es obligatoria
            </div>
          </div>

          <!-- Descripción -->
          <div class="input-block">
            <label>Descripción</label>
            <textarea
              formControlName="descripcion"
              rows="2"
              placeholder="Describa la tarea..."
              [class.has-error]="
                tareaForm.get('descripcion')?.touched && tareaForm.get('descripcion')?.invalid
              "
            ></textarea>
            <div
              *ngIf="tareaForm.get('descripcion')?.touched && tareaForm.get('descripcion')?.invalid"
              class="error-text"
            >
              ⚠️ Máximo 500 caracteres
            </div>
          </div>

          <!-- Estado -->
          <div class="input-block">
            <label>Estado <span class="required-mark">*</span></label>
            <select formControlName="estado">
              <option *ngFor="let est of estados" [value]="est">{{ est }}</option>
            </select>
          </div>

          <div class="actions-wrapper">
            <button type="button" (click)="cerrar()" class="btn-cancel">Cancelar</button>
            <button type="submit" [disabled]="tareaForm.invalid" class="btn-save">
              {{ editMode ? 'Actualizar' : 'Guardar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [
    `
      .error-text {
        color: #dc2626;
        font-size: 0.75rem;
        margin-top: 4px;
      }
      .has-error {
        border-color: #dc2626 !important;
      }
      .input-block {
        margin-bottom: 16px;
      }
      .input-block label {
        display: block;
        font-weight: 600;
        margin-bottom: 4px;
      }
      .required-mark {
        color: #dc2626;
      }
      select,
      input,
      textarea {
        width: 100%;
        padding: 8px 12px;
        border: 1px solid #d1d5db;
        border-radius: 6px;
        font-size: 14px;
      }
      select:focus,
      input:focus,
      textarea:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
      }
      textarea {
        resize: vertical;
        min-height: 60px;
      }
      .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 1000;
      }
      .modal-card {
        background: white;
        border-radius: 12px;
        padding: 24px;
        max-width: 600px;
        width: 100%;
        max-height: 90vh;
        overflow-y: auto;
      }
      .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
      }
      .modal-header h3 {
        margin: 0;
        font-size: 1.25rem;
        color: #1a1a2e;
      }
      .btn-close {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #6b7280;
      }
      .btn-close:hover {
        color: #1a1a2e;
      }
      .actions-wrapper {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        margin-top: 20px;
        padding-top: 16px;
        border-top: 1px solid #e5e7eb;
      }
      .btn-cancel {
        padding: 8px 20px;
        background: #f3f4f6;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 500;
      }
      .btn-cancel:hover {
        background: #e5e7eb;
      }
      .btn-save {
        padding: 8px 20px;
        background: #667eea;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 500;
      }
      .btn-save:hover {
        background: #5466ca;
      }
      .btn-save:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    `,
  ],
})
export class TareaFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private tareaService = inject(TareaAsignadaService);
  private empleadoService = inject(EmpleadoService);
  private areaService = inject(AreaTrabajoService);

  @Input() tareaData: TareaAsignadaResponse | null = null;
  @Input() editMode = false;

  @Output() onCerrar = new EventEmitter<void>();
  @Output() onExito = new EventEmitter<void>();

  tareaForm!: FormGroup;
  empleados: any[] = [];
  supervisores: any[] = [];
  areas: any[] = [];
  funciones = Object.values(FuncionTarea);
  estados = Object.values(EstadoTarea);

  ngOnInit(): void {
    this.initForm();
    this.cargarDatos();
    if (this.editMode && this.tareaData) {
      this.tareaForm.patchValue({
        empleadoId: this.tareaData.empleadoId,
        supervisorId: this.tareaData.supervisorId,
        areaId: this.tareaData.areaId,
        funcion: this.tareaData.funcion,
        fecha: this.tareaData.fecha,
        descripcion: this.tareaData.descripcion,
        estado: this.tareaData.estado,
      });
    }
  }

  fechaNoPasada(control: any) {
    const fechaSeleccionada = new Date(control.value);
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    return fechaSeleccionada >= hoy ? null : { fechaPasada: true };
  }

  initForm(): void {
    this.tareaForm = this.fb.group({
      empleadoId: [null, [Validators.required]],
      supervisorId: [null, [Validators.required]],
      areaId: [null, [Validators.required]],
      funcion: ['', [Validators.required]],

      fecha: ['', [Validators.required, this.fechaNoPasada]],
      descripcion: ['', [Validators.maxLength(500)]],
      estado: ['PENDIENTE', [Validators.required]],
    });
  }

  cargarDatos(): void {
    // ✅ Empleados: SOLO los que tienen rol TRABAJADOR o EMPLEADO
    this.empleadoService.listarTrabajadoresByRol().subscribe({
      next: (data) => {
        this.empleados = data;
      },
      error: (err) => {
        console.error('Error al cargar empleados:', err);
      },
    });

    this.empleadoService.listarSupervisoresByRol().subscribe({
      next: (data) => {
        this.supervisores = data;
      },
      error: (err) => {
        console.error('Error al cargar supervisores:', err);
      },
    });

    this.areaService.listarActivas().subscribe({
      next: (data) => {
        this.areas = data;
      },
      error: (err) => {
        console.error('Error al cargar áreas:', err);
      },
    });
  }

  cerrar(): void {
    this.onCerrar.emit();
  }

  onSubmit(): void {
    if (this.tareaForm.invalid) {
      this.tareaForm.markAllAsTouched();
      return;
    }

    const dto = this.tareaForm.value;
    if (this.editMode && this.tareaData) {
      this.tareaService.actualizar(this.tareaData.id, dto).subscribe({
        next: () => this.onExito.emit(),
        error: (err) => console.error('Error al actualizar:', err),
      });
    } else {
      this.tareaService.crear(dto).subscribe({
        next: () => this.onExito.emit(),
        error: (err) => console.error('Error al crear:', err),
      });
    }
  }
}
