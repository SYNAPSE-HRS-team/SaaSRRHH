import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';

import {
  AttendanceDashboardService,
  DashboardResumen,
  RankingTardanza,
  RegistroAsistenciaResponse,
  ValidacionSeguridadResponse,
} from './attendance-dashboard.service';

interface TarjetaResumen {
  titulo: string;
  valor: string;
  subtitulo: string;
  tono: string;
}

@Component({
  selector: 'app-human-resources',
  templateUrl: './human-resources.component.html',
  styleUrls: ['./human-resources.component.scss'],
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

  empleadoMarcacionId = '';
  metodoMarcacion = 'Manual';
  tipoMarcacionManual: 'entrada' | 'salida' = 'entrada';

  qrEmpleadoId = '';
  qrTotpHash = '';
  tipoMarcacionQr: 'entrada' | 'salida' = 'entrada';
  validacionQr?: ValidacionSeguridadResponse;

  accionEnCurso = false;
  qrAccionEnCurso = false;
  mensajeAccion = '';
  mensajeQr = '';

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
        this.rankingTardanzas = (rankingTardanzas || [])
          .map((item: any) => {
            const empleadoId = Array.isArray(item) ? item[0] : item.empleadoId;
            const tardanzas = Array.isArray(item) ? item[1] : item.tardanzas;

            return {
              empleadoId: Number(empleadoId) || 0,
              tardanzas: Number(tardanzas) || 0,
            };
          })
          .filter((item) => item.empleadoId > 0)
          .sort((a, b) => b.tardanzas - a.tardanzas)
          .slice(0, 5);
        this.tarjetasResumen = this.crearTarjetasResumen();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar las metricas de asistencias.';
        this.tarjetasResumen = this.crearTarjetasResumen();
        this.loading = false;
      },
    });
  }

  marcarEntrada(): void {
    this.tipoMarcacionManual = 'entrada';
    this.registrarManual();
  }

  marcarSalida(): void {
    this.tipoMarcacionManual = 'salida';
    this.registrarManual();
  }

  registrarManual(): void {
    this.marcarAsistencia(this.tipoMarcacionManual, this.metodoMarcacion || 'Manual');
  }

  validarQr(): void {
    const hash = this.qrTotpHash.trim();

    if (!hash) {
      this.mensajeQr = 'Ingresa el hash TOTP leido desde el QR.';
      this.validacionQr = undefined;
      return;
    }

    this.qrAccionEnCurso = true;
    this.mensajeQr = '';

    this.attendanceDashboardService.obtenerValidacionesSeguridad().subscribe({
      next: (validaciones) => {
        this.validacionQr = (validaciones || []).find((validacion) =>
          validacion.totpValido === true && validacion.totpHash === hash
        );

        this.mensajeQr = this.validacionQr
          ? 'Codigo QR validado. Ya puedes registrar la marcacion.'
          : 'El codigo TOTP no existe o no esta marcado como valido.';
        this.qrAccionEnCurso = false;
      },
      error: () => {
        this.mensajeQr = 'No se pudo consultar la validacion de seguridad.';
        this.validacionQr = undefined;
        this.qrAccionEnCurso = false;
      },
    });
  }

  registrarQr(): void {
    const empleadoId = Number(this.qrEmpleadoId);

    if (!empleadoId || empleadoId <= 0) {
      this.mensajeQr = 'Ingresa un ID de empleado valido para asociar el QR.';
      return;
    }

    if (!this.validacionQr || this.validacionQr.totpHash !== this.qrTotpHash.trim()) {
      this.mensajeQr = 'Valida primero el codigo TOTP del QR.';
      return;
    }

    this.marcarAsistencia(this.tipoMarcacionQr, 'QR', empleadoId, true);
  }

  simularEscaneoQr(): void {
    this.qrTotpHash = this.qrTotpHash || 'abc123...';
    this.validacionQr = undefined;
    this.mensajeQr = 'Hash cargado desde el lector QR simulado.';
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

  formatearHora(fecha?: string): string {
    return fecha
      ? new Date(fecha).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
      : '-';
  }

  limpiarMensaje(): void {
    this.mensajeAccion = '';
    this.mensajeQr = '';
  }

  private marcarAsistencia(
    tipo: 'entrada' | 'salida',
    metodo: string,
    empleadoIdExterno?: number,
    desdeQr = false,
  ): void {
    const empleadoId = empleadoIdExterno || Number(this.empleadoMarcacionId);

    if (!empleadoId || empleadoId <= 0) {
      const mensaje = 'Ingresa un ID de empleado valido.';
      this.mensajeAccion = mensaje;
      if (desdeQr) {
        this.mensajeQr = mensaje;
      }
      return;
    }

    if (desdeQr) {
      this.qrAccionEnCurso = true;
      this.mensajeQr = '';
    } else {
      this.accionEnCurso = true;
      this.mensajeAccion = '';
    }

    const solicitud = tipo === 'entrada'
      ? this.attendanceDashboardService.registrarEntrada(empleadoId, metodo)
      : this.attendanceDashboardService.registrarSalida(empleadoId, metodo);

    solicitud.subscribe({
      next: () => {
        const mensaje = tipo === 'entrada'
          ? 'Entrada registrada correctamente.'
          : 'Salida registrada correctamente.';
        if (desdeQr) {
          this.mensajeQr = mensaje;
          this.validacionQr = undefined;
        } else {
          this.mensajeAccion = mensaje;
        }
        this.cargarDatos();
        this.accionEnCurso = false;
        this.qrAccionEnCurso = false;
      },
      error: () => {
        const mensaje = tipo === 'entrada'
          ? 'No se pudo registrar la entrada.'
          : 'No se pudo registrar la salida.';
        if (desdeQr) {
          this.mensajeQr = mensaje;
        } else {
          this.mensajeAccion = mensaje;
        }
        this.accionEnCurso = false;
        this.qrAccionEnCurso = false;
      },
    });
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
        subtitulo: 'Entradas y salidas',
        tono: 'tono-azul',
      },
      {
        titulo: 'Entradas',
        valor: String(this.entradasHoy),
        subtitulo: 'Primer ingreso del dia',
        tono: 'tono-verde',
      },
      {
        titulo: 'Salidas',
        valor: String(this.salidasHoy),
        subtitulo: 'Cierre de jornada',
        tono: 'tono-naranja',
      },
      {
        titulo: 'Incidencias',
        valor: String(this.incidencias.length),
        subtitulo: 'Marcaciones con observacion',
        tono: 'tono-rojo',
      },
    ];
  }
}
