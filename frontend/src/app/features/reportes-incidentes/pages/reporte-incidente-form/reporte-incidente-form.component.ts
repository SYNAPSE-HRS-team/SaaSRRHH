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

  tiposIncidente = Object.values(TipoIncidente);
  nivelesRiesgo = Object.values(NivelRiesgo);
  estadosIncidente = Object.values(EstadoIncidente);

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private reporteService: ReporteIncidenteService,
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
    this.verificarParametros();
  }

  inicializarFormulario(): void {
    this.form = this.fb.group({
      empleadoId: ['', [Validators.required]],
      supervisorId: [''],
      tareaId: [''],
      areaId: [''],
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
        this.form.patchValue({
          empleadoId: data.empleadoId,
          supervisorId: data.supervisorId || '',
          tareaId: data.tareaId || '',
          areaId: data.areaId || '',
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

  onSubmit(): void {
    this.submitted = true;
    this.error = false;

    if (this.form.invalid) {
      return;
    }

    this.loading = true;
    const dto: ReporteIncidenteRequest = this.form.value;

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
