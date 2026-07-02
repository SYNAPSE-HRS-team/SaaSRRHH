import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
    EstadoIncidente,
    NivelRiesgo,
    ReporteIncidenteRequest,
    TipoIncidente,
} from '../../../../core/models/reporte-incidente.model';
import { ReporteIncidenteService } from '../../../../core/services/reporte-incidente.service';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';
import { TareaAsignadaResponse } from '../../../../core/models/tarea-asignada.model';

@Component({
  selector: 'app-reporte-incidente-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reporte-incidente-form.component.html',
  styleUrls: ['./reporte-incidente-form.component.scss'],
})
export class ReporteIncidenteFormComponent implements OnInit {
  form!: FormGroup;
  esEdicion = false;
  esVisualizacion = false;
  idIncidente?: number;
  loading = false;
  submitted = false;
  error = false;
  errorMessage = '';

  tareas: TareaAsignadaResponse[] = [];
  tiposIncidente = Object.values(TipoIncidente);
  nivelesRiesgo = Object.values(NivelRiesgo);
  estadosIncidente = Object.values(EstadoIncidente);

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private reporteService: ReporteIncidenteService,
    private tareaService: TareaAsignadaService,
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
    this.cargarTareas();
  }

  inicializarFormulario(): void {
    this.form = this.fb.group({
      tareaId: ['', [Validators.required]],
      tipo: ['', [Validators.required]],
      descripcion: ['', [Validators.required, Validators.maxLength(1000)]],
      evidenciaUrl: ['', [Validators.maxLength(500)]],
      nivelRiesgo: [''],
      estado: [''],
      fechaIncidente: ['', [Validators.required]],
    });
  }

  verificarParametros(): void {
    this.route.params.subscribe((params) => {
      const id = params['id'];
      const url = this.router.url;

      if (url.includes('/ver/')) {
        this.esVisualizacion = true;
        this.idIncidente = +id;
        this.cargarReporte(this.idIncidente, true);
      } else if (url.includes('/editar/')) {
        this.esEdicion = true;
        this.idIncidente = +id;
        this.cargarReporte(this.idIncidente, false);
      } else {
        this.form.patchValue({
          fechaIncidente: new Date().toISOString().slice(0, 16),
        });
      }
    });
  }

  cargarReporte(id: number, soloLectura: boolean): void {
    this.loading = true;
    this.reporteService.obtenerPorId(id).subscribe({
      next: (data: any) => {
        const tarea = this.tareas.find((t) => t.id === data.tareaId);

        this.form.patchValue({
          tareaId: data.tareaId || '',
          tipo: data.tipo,
          descripcion: data.descripcion,
          evidenciaUrl: data.evidenciaUrl || '',
          nivelRiesgo: data.nivelRiesgo || '',
          estado: data.estado || '',
          fechaIncidente: new Date(data.fechaIncidente).toISOString().slice(0, 16),
        });

        if (soloLectura) {
          this.form.disable();
        }

        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error al cargar reporte:', err);
        this.error = true;
        this.errorMessage = 'No se pudo cargar el reporte';
        this.loading = false;
      },
    });
  }

  get f() {
    return this.form.controls;
  }

  get selectedTarea(): TareaAsignadaResponse | undefined {
    const tareaId = this.form.get('tareaId')?.value;
    return this.tareas.find((t) => t.id === Number(tareaId));
  }

  cargarTareas(): void {
    this.tareaService.listar().subscribe({
      next: (data) => {
        this.tareas = data;
        this.verificarParametros();
      },
      error: (err) => {
        console.error('Error al cargar tareas:', err);
      },
    });
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = false;

    if (this.form.invalid) {
      return;
    }

    const selectedTarea = this.selectedTarea;
    if (!selectedTarea) {
      this.error = true;
      this.errorMessage = 'Debe seleccionar una tarea válida para crear el incidente';
      return;
    }

    this.loading = true;
    const dto: ReporteIncidenteRequest = {
      tareaId: selectedTarea.id,
      areaId: selectedTarea.areaId || selectedTarea.area?.id,
      empleadoId: selectedTarea.empleadoId || selectedTarea.empleado?.id!,
      supervisorId: selectedTarea.supervisorId || selectedTarea.supervisor?.id,
      tipo: this.form.value.tipo,
      descripcion: this.form.value.descripcion,
      evidenciaUrl: this.form.value.evidenciaUrl,
      nivelRiesgo: this.form.value.nivelRiesgo,
      estado: this.form.value.estado,
      fechaIncidente: this.form.value.fechaIncidente,
    };

    if (this.esEdicion && this.idIncidente) {
      this.actualizarReporte(this.idIncidente, dto);
    } else {
      this.crearReporte(dto);
    }
  }

  crearReporte(dto: ReporteIncidenteRequest): void {
    this.reporteService.crear(dto).subscribe({
      next: () => {
        this.router.navigate(['/reportes-incidentes']);
      },
      error: (err: any) => {
        console.error('Error al crear:', err);
        this.error = true;
        this.errorMessage = 'Error al crear el reporte';
        this.loading = false;
      },
    });
  }

  actualizarReporte(id: number, dto: ReporteIncidenteRequest): void {
    this.reporteService.actualizar(id, dto).subscribe({
      next: () => {
        this.router.navigate(['/reportes-incidentes']);
      },
      error: (err: any) => {
        console.error('Error al actualizar:', err);
        this.error = true;
        this.errorMessage = 'Error al actualizar el reporte';
        this.loading = false;
      },
    });
  }

  cancelar(): void {
    this.router.navigate(['/reportes-incidentes']);
  }

  getTitulo(): string {
    if (this.esVisualizacion) return 'Ver Reporte de Incidente';
    if (this.esEdicion) return 'Editar Reporte de Incidente';
    return 'Nuevo Reporte de Incidente';
  }
}
