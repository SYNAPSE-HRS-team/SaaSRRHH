import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';

import {
  AttendanceDashboardService,
  DashboardResumen,
  RankingTardanza,
  RegistroAsistenciaResponse,
} from './attendance-dashboard.service';

interface TarjetaResumen {
  titulo: string;
  valor: string;
  subtitulo: string;
  icono: string;
  tono: string;
}

@Component({
  selector: 'app-human-resources',
  templateUrl: './human-resources.component.html',
  styleUrls: ['./human-resources.component.scss']
})
export class HumanResourcesComponent implements OnInit {

  loading = true;
  errorMessage = '';

  dashboard: DashboardResumen = {
    totalEmpleados: 0,
    totalUsuarios: 0,
    reportesDiarios: 0,
    ausencias: 0,
    incidentes: 0,
    porcentajeAusentismo: 0,
    nivelRiesgo: 'BAJO',
  };

  tarjetasResumen: TarjetaResumen[] = [];

  marcacionesHoy: RegistroAsistenciaResponse[] = [];

  incidencias: RegistroAsistenciaResponse[] = [];

  rankingTardanzas: RankingTardanza[] = [];

  constructor(
    private readonly attendanceDashboardService: AttendanceDashboardService,
  ) { }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      dashboard: this.attendanceDashboardService.obtenerDashboard(),
      asistenciasHoy: this.attendanceDashboardService.obtenerAsistenciasHoy(),
      incidencias: this.attendanceDashboardService.obtenerIncidencias(),
      rankingTardanzas: this.attendanceDashboardService.obtenerRankingTardanzas(),
    }).subscribe({
      next: ({ dashboard, asistenciasHoy, incidencias, rankingTardanzas }) => {
        this.dashboard = dashboard;
        this.marcacionesHoy = this.ordenarPorFechaDesc(asistenciasHoy);
        this.incidencias = this.ordenarPorFechaDesc(incidencias);
        this.rankingTardanzas = rankingTardanzas
          .map(([empleadoId, tardanzas]) => ({
            empleadoId: Number(empleadoId),
            tardanzas: Number(tardanzas),
          }))
          .sort((a, b) => b.tardanzas - a.tardanzas)
          .slice(0, 5);
        this.tarjetasResumen = this.crearTarjetasResumen();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar las métricas de asistencias.';
        this.tarjetasResumen = this.crearTarjetasResumen();
        this.loading = false;
      },
    });
  }

  get entradasHoy(): number {
    return this.marcacionesHoy.filter((registro) => registro.tipoMarcacion === 'ENTRADA').length;
  }

  get salidasHoy(): number {
    return this.marcacionesHoy.filter((registro) => registro.tipoMarcacion === 'SALIDA').length;
  }

  get marcacionesValidasHoy(): number {
    return this.marcacionesHoy.filter((registro) => registro.estado === 'VALIDADO').length;
  }

  get porcentajeAusentismoSeguro(): number {
    return Math.min(100, Math.max(0, this.dashboard.porcentajeAusentismo || 0));
  }

  esEntrada(tipoMarcacion?: string): boolean {
    return (tipoMarcacion || '').toUpperCase() === 'ENTRADA';
  }

  claseEstado(estado?: string): string {
    switch ((estado || '').toUpperCase()) {
      case 'VALIDADO':
        return 'badge-validado';
      case 'RECHAZADO':
        return 'badge-rechazado';
      default:
        return 'badge-pendiente';
    }
  }

  claseMarcacion(tipoMarcacion?: string): string {
    return this.esEntrada(tipoMarcacion) ? 'marcacion-entrada' : 'marcacion-salida';
  }

  formatearFecha(fecha?: string): string {
    return fecha ? new Date(fecha).toLocaleString('es-ES') : 'Sin fecha';
  }

  private ordenarPorFechaDesc(registros: RegistroAsistenciaResponse[]): RegistroAsistenciaResponse[] {
    return [...(registros || [])].sort((a, b) => {
      const fechaA = new Date(a.fechaHora).getTime();
      const fechaB = new Date(b.fechaHora).getTime();

      return fechaB - fechaA;
    });
  }

  private crearTarjetasResumen(): TarjetaResumen[] {
    return [
      {
        titulo: 'Marcaciones hoy',
        valor: String(this.marcacionesHoy.length),
        subtitulo: 'Entradas y salidas registradas',
        icono: 'bx bx-time-five',
        tono: 'tono-azul',
      },
      {
        titulo: 'Entradas',
        valor: String(this.entradasHoy),
        subtitulo: 'Primer ingreso del día',
        icono: 'bx bx-log-in',
        tono: 'tono-verde',
      },
      {
        titulo: 'Salidas',
        valor: String(this.salidasHoy),
        subtitulo: 'Cierres de jornada',
        icono: 'bx bx-log-out',
        tono: 'tono-naranja',
      },
      {
        titulo: 'Incidencias',
        valor: String(this.incidencias.length),
        subtitulo: 'Marcaciones con observación',
        icono: 'bx bx-error-circle',
        tono: 'tono-rojo',
      },
    ];
  }
}
