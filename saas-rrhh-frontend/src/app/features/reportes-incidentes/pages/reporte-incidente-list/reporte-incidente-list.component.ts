import { CommonModule } from '@angular/common'; // ← AGREGAR
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms'; // ← AGREGAR
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import {
    EstadoIncidente,
    NivelRiesgo,
    ReporteIncidenteResponse,
    TipoIncidente,
} from '../../../../core/models/reporte-incidente.model';
import { ReporteIncidenteService } from '../../../../core/services/reporte-incidente.service';

@Component({
  selector: 'app-reporte-incidente-list',
  standalone: true, // ← AGREGAR
  imports: [CommonModule, FormsModule], // ← AGREGAR
  templateUrl: './reporte-incidente-list.component.html',
  styleUrls: ['./reporte-incidente-list.component.scss'],
})
export class ReporteIncidenteListComponent implements OnInit, OnDestroy {
  reportes: ReporteIncidenteResponse[] = [];
  reportesFiltrados: ReporteIncidenteResponse[] = [];
  loading = false;
  error = false;

  filtros = {
    nivelRiesgo: '',
    estado: '',
    tipo: '',
    fechaInicio: '',
    fechaFin: '',
  };

  nivelesRiesgo = Object.values(NivelRiesgo);
  estadosIncidente = Object.values(EstadoIncidente);
  tiposIncidente = Object.values(TipoIncidente);

  private subscriptions: Subscription = new Subscription();

  totalReportes = 0;
  totalCriticos = 0;
  totalHoy = 0;

  constructor(
    private reporteService: ReporteIncidenteService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.cargarReportes();
    this.cargarEstadisticas();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  cargarReportes(): void {
    this.loading = true;
    this.error = false;

    this.subscriptions.add(
      this.reporteService.listar().subscribe({
        next: (data: ReporteIncidenteResponse[]) => {
          this.reportes = data;
          this.aplicarFiltros();
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Error al cargar reportes:', err);
          this.error = true;
          this.loading = false;
        },
      }),
    );
  }

  cargarEstadisticas(): void {
    this.subscriptions.add(
      this.reporteService.listarCriticos().subscribe({
        next: (data: ReporteIncidenteResponse[]) => {
          this.totalCriticos = data.length;
        },
        error: (err: any) => console.error('Error al cargar críticos:', err),
      }),
    );

    this.subscriptions.add(
      this.reporteService.listarDeHoy().subscribe({
        next: (data: ReporteIncidenteResponse[]) => {
          this.totalHoy = data.length;
        },
        error: (err: any) => console.error('Error al cargar de hoy:', err),
      }),
    );
  }

  aplicarFiltros(): void {
    this.reportesFiltrados = this.reportes.filter((reporte: ReporteIncidenteResponse) => {
      let coincide = true;

      if (this.filtros.nivelRiesgo && reporte.nivelRiesgo !== this.filtros.nivelRiesgo) {
        coincide = false;
      }

      if (this.filtros.estado && reporte.estado !== this.filtros.estado) {
        coincide = false;
      }

      if (this.filtros.tipo && reporte.tipo !== this.filtros.tipo) {
        coincide = false;
      }

      if (this.filtros.fechaInicio) {
        const fechaInicio = new Date(this.filtros.fechaInicio);
        const fechaIncidente = new Date(reporte.fechaIncidente);
        if (fechaIncidente < fechaInicio) {
          coincide = false;
        }
      }

      if (this.filtros.fechaFin) {
        const fechaFin = new Date(this.filtros.fechaFin);
        const fechaIncidente = new Date(reporte.fechaIncidente);
        if (fechaIncidente > fechaFin) {
          coincide = false;
        }
      }

      return coincide;
    });

    this.totalReportes = this.reportesFiltrados.length;
  }

  limpiarFiltros(): void {
    this.filtros = {
      nivelRiesgo: '',
      estado: '',
      tipo: '',
      fechaInicio: '',
      fechaFin: '',
    };
    this.aplicarFiltros();
  }

  nuevoReporte(): void {
    this.router.navigate(['/reportes-incidentes/nuevo']);
  }

  editarReporte(id: number): void {
    this.router.navigate(['/reportes-incidentes/editar', id]);
  }

  verReporte(id: number): void {
    this.router.navigate(['/reportes-incidentes/ver', id]);
  }

  eliminarReporte(id: number): void {
    if (confirm('¿Estás seguro de eliminar este reporte?')) {
      this.subscriptions.add(
        this.reporteService.eliminar(id).subscribe({
          next: () => {
            this.cargarReportes();
            this.cargarEstadisticas();
          },
          error: (err: any) => {
            console.error('Error al eliminar:', err);
            alert('Error al eliminar el reporte');
          },
        }),
      );
    }
  }

  getColorRiesgo(nivel: string): string {
    const colores: any = {
      BAJO: 'success',
      MEDIO: 'warning',
      ALTO: 'danger',
      CRITICO: 'danger',
    };
    return colores[nivel] || 'secondary';
  }

  getBadgeEstado(estado: string): string {
    const badges: any = {
      REPORTADO: 'warning',
      EN_REVISION: 'info',
      CERRADO: 'success',
    };
    return badges[estado] || 'secondary';
  }
}
