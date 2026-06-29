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
  templateUrl: './tarea-form.component.html',
  styleUrls: ['./tarea-form.component.scss']
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