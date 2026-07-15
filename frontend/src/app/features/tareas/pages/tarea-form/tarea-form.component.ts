import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import {
  EstadoTarea,
  FuncionTarea,
  TareaAsignadaResponse,
} from '../../../../core/models/tarea-asignada.model';
import { AreaTrabajoService } from '../../../../core/services/area-trabajo.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-tarea-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule],
  templateUrl: './tarea-form.component.html',
  styleUrls: ['./tarea-form.component.scss'],
})
export class TareaFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private tareaService = inject(TareaAsignadaService);
  private empleadoService = inject(EmpleadoService);
  private areaService = inject(AreaTrabajoService);
  private authService = inject(AuthService);

  @Input() tareaData: TareaAsignadaResponse | null = null;
  @Input() editMode = false;
  @Input() soloLectura = false; // ✅ NUEVO: para ver tareas cerradas

  @Output() onCerrar = new EventEmitter<void>();
  @Output() onExito = new EventEmitter<void>();

  tareaForm!: FormGroup;
  empleados: any[] = [];
  supervisores: any[] = [];
  areas: any[] = [];
  funciones = Object.values(FuncionTarea);
  estados = Object.values(EstadoTarea);

  // ✅ Estados que bloquean la edición
  estadosBloqueados = [EstadoTarea.COMPLETADO, EstadoTarea.CANCELADO, EstadoTarea.INCONCLUSO];

  // ✅ Estados disponibles según modo (CREAR vs EDITAR)
  get estadosDisponibles(): string[] {
    if (this.editMode) {
      return this.estados;
    } else {
      return [EstadoTarea.PENDIENTE, EstadoTarea.EN_PROGRESO];
    }
  }

  // ✅ Verificar si el formulario está bloqueado
  get isFormLocked(): boolean {
    if (this.soloLectura) return true;
    
    // Si es ADMIN o SUPERVISOR, no se bloquea la edición del estado
    const role = this.authService.getCurrentUser()?.rol;
    if (role === 'ADMIN' || role === 'SUPERVISOR') return false;

    if (!this.editMode) return false;
    if (!this.tareaData) return false;
    const estado = this.tareaData.estado;
    if (!estado) return false;
    return this.estadosBloqueados.includes(estado as EstadoTarea);
  }

  ngOnInit(): void {
    this.initForm();
    this.cargarDatos();

    if (this.editMode && this.tareaData) {
      const fechaFormateada = this.tareaData.fecha
        ? new Date(this.tareaData.fecha).toISOString().split('T')[0]
        : null;
      this.tareaForm.patchValue({
        empleadoId: this.tareaData.empleadoId,
        supervisorId: this.tareaData.supervisorId,
        areaId: this.tareaData.areaId,
        funcion: this.tareaData.funcion,
        fecha: fechaFormateada,
        descripcion: this.tareaData.descripcion,
        estado: this.tareaData.estado,
      });

      // ✅ Si está bloqueado, deshabilitar el formulario
      if (this.isFormLocked) {
        this.tareaForm.disable();
      }
    }

    // ✅ Si es solo lectura, deshabilitar todo
    if (this.soloLectura) {
      this.tareaForm.disable();
    }
  }

  fechaNoPasada(control: any) {
    if (this.editMode) {
      return null;
    }
    if (!control.value) {
      return null;
    }

    const fechaParts = control.value.split('-');
    const fechaSeleccionada = new Date(
      parseInt(fechaParts[0]),
      parseInt(fechaParts[1]) - 1,
      parseInt(fechaParts[2]),
    );

    const hoy = new Date();
    const hoyLocal = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());

    const diffTime = fechaSeleccionada.getTime() - hoyLocal.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    return diffDays >= 0 ? null : { fechaPasada: true };
  }

  initForm(): void {
    const hoy = new Date();
    const fechaHoy = hoy.toISOString().split('T')[0];

    this.tareaForm = this.fb.group({
      empleadoId: [null, [Validators.required]],
      supervisorId: [null, [Validators.required]],
      areaId: [null, [Validators.required]],
      funcion: ['', [Validators.required]],
      fecha: [fechaHoy, [Validators.required, this.fechaNoPasada.bind(this)]],
      descripcion: ['', [Validators.maxLength(500)]],
      estado: [EstadoTarea.PENDIENTE, [Validators.required]],
    });
  }

  cargarDatos(): void {
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
    // ✅ Si está bloqueado, no permitir enviar
    if (this.isFormLocked) {
      alert('Esta tarea está finalizada y no se puede modificar.');
      return;
    }

    if (this.tareaForm.invalid) {
      this.tareaForm.markAllAsTouched();
      return;
    }

    const dto = this.tareaForm.value;
    if (this.editMode && this.tareaData) {
      this.tareaService.actualizar(this.tareaData.id, dto).subscribe({
        next: () => {
          alert('Tarea actualizada con éxito.');
          this.onExito.emit();
        },
        error: (err) => {
          console.error('Error al actualizar:', err);
          alert('Error al actualizar la tarea: ' + (err.error?.message || err.message || 'Error desconocido'));
        },
      });
    } else {
      this.tareaService.crear(dto).subscribe({
        next: () => {
          alert('Tarea asignada con éxito.');
          this.onExito.emit();
        },
        error: (err) => {
          console.error('Error al crear:', err);
          alert('Error al asignar la tarea: ' + (err.error?.message || err.message || 'Error desconocido'));
        },
      });
    }
  }
}
