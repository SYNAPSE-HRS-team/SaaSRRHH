import { Component, OnInit, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TareaAsignada } from '../../../../core/models/tarea-asignada.model'; //

@Component({
  selector: 'app-tarea-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  styleUrls: ['./tarea-form.component.scss'], // SASS externo
  template: `
    <div class="modal-overlay">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ editMode ? 'Editar Parámetros de Tarea' : 'Asignar Nueva Tarea' }}</h3>
          <button (click)="cerrar()" class="btn-close">&times;</button>
        </div>

        <form [formGroup]="tareaForm" (ngSubmit)="onSubmit()" class="modal-body">
          
          <div class="input-block">
            <label>Título de Actividad <span class="required-mark">*</span></label>
            <input type="text" formControlName="titulo" [ngClass]="{'has-error': tareaForm.get('titulo')?.touched && tareaForm.get('titulo')?.invalid}" placeholder="Ej. Rediseñar reportes"/>
            <div *ngIf="tareaForm.get('titulo')?.touched && tareaForm.get('titulo')?.invalid" class="error-wrapper">
              <small *ngIf="tareaForm.get('titulo')?.errors?.['required']" class="error-text">⚠️ El título es un campo obligatorio.</small>
              <small *ngIf="tareaForm.get('titulo')?.errors?.['minlength']" class="error-text">⚠️ Debe contener al menos 5 letras.</small>
            </div>
          </div>

          <div class="input-block">
            <label>Descripción / Observaciones</label>
            <textarea formControlName="descripcion" rows="2" placeholder="Describa el alcance de la tarea..."></textarea>
          </div>

          <div class="input-grid">
            <div class="input-block">
              <label>Prioridad <span class="required-mark">*</span></label>
              <select formControlName="prioridad">
                <option value="ALTA">ALTA</option>
                <option value="MEDIA">MEDIA</option>
                <option value="BAJA">BAJA</option>
              </select>
            </div>

            <div class="input-block">
              <label>Estado Inicial <span class="required-mark">*</span></label>
              <select formControlName="estado">
                <option value="PENDIENTE">PENDIENTE</option>
                <option value="EN_PROCESO">EN PROCESO</option>
                <option value="COMPLETADA">COMPLETADA</option>
              </select>
            </div>
          </div>

          <div class="input-block">
            <label>Colaborador Responsable <span class="required-mark">*</span></label>
            <select formControlName="idEmpleado" [ngClass]="{'has-error': tareaForm.get('idEmpleado')?.touched && tareaForm.get('idEmpleado')?.invalid}">
              <option [ngValue]="null" disabled>Selecciona un trabajador del equipo...</option>
              <option *ngFor="let emp of empleados" [value]="emp.id">{{ emp.nombres }}</option>
            </select>
            <div *ngIf="tareaForm.get('idEmpleado')?.touched && tareaForm.get('idEmpleado')?.invalid" class="error-wrapper">
              <small class="error-text">⚠️ Debes asignar un empleado responsable.</small>
            </div>
          </div>

          <div class="input-block">
            <label>Fecha de Vencimiento <span class="required-mark">*</span></label>
            <input type="date" formControlName="fechaVencimiento" [ngClass]="{'has-error': tareaForm.get('fechaVencimiento')?.touched && tareaForm.get('fechaVencimiento')?.invalid}"/>
            <div *ngIf="tareaForm.get('fechaVencimiento')?.touched && tareaForm.get('fechaVencimiento')?.invalid" class="error-wrapper">
              <small class="error-text">⚠️ La fecha de vencimiento es obligatoria.</small>
            </div>
          </div>

          <div class="actions-wrapper">
            <button type="button" (click)="cerrar()" class="btn-cancel">Cancelar</button>
            <button type="submit" [disabled]="tareaForm.invalid" class="btn-save">Confirmar Cambios</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class TareaFormComponent implements OnInit {
  private fb = inject(FormBuilder);

  @Input() tareaData: TareaAsignada | null = null; // Tipado real
  @Input() editMode = false;

  @Output() onCerrar = new EventEmitter<void>();
  @Output() onExito = new EventEmitter<void>();

  tareaForm!: FormGroup;

  // Lista simulada que coincide con lo que regresaría EmpleadoResponse
  empleados: any[] = [
    { id: 1, nombres: 'Carlos Mendoza' },
    { id: 2, nombres: 'Ana Gómez' },
    { id: 3, nombres: 'Juan Pérez' }
  ];

  ngOnInit(): void {
    this.initForm();
    if (this.editMode && this.tareaData) {
      // Seteamos los datos respetando la correspondencia del formulario reactivo
      this.tareaForm.patchValue({
        idTarea: this.tareaData.idTarea,
        titulo: this.tareaData.titulo,
        descripcion: this.tareaData.descripcion,
        prioridad: this.tareaData.prioridad,
        estado: this.tareaData.estado,
        fechaVencimiento: this.tareaData.fechaVencimiento,
        idEmpleado: this.tareaData.idEmpleado || this.tareaData.empleado?.id
      });
    }
  }

  initForm(): void {
    this.tareaForm = this.fb.group({
      idTarea: [null],
      titulo: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      descripcion: ['', [Validators.maxLength(255)]],
      prioridad: ['MEDIA', [Validators.required]],
      estado: ['PENDIENTE', [Validators.required]],
      fechaVencimiento: ['', [Validators.required]], // Propiedad de tu interfaz
      idEmpleado: [null, [Validators.required]] // ID relacional del modelo
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
    this.onExito.emit();
  }
}