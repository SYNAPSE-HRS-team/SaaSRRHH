import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { interval, Subscription } from 'rxjs';

import { AuthService } from '../../auth/auth.service';
import { EmpleadoResponse } from '../human-resources/attendance-dashboard.service';

interface BoletaPagoResponse {
  id: number;
  empleadoId: number;
  planillaId?: number | null;
  sueldoBase?: number | null;
  diasTrabajados?: number | null;
  diasNoTrabajados?: number | null;
  totalIngresos?: number | null;
  totalDescuentos?: number | null;
  netoPagar?: number | null;
  fechaEmision?: string | null;
}

@Component({
  selector: 'app-employee-attendance',
  templateUrl: './employee-attendance.component.html',
  styleUrls: ['./employee-attendance.component.scss'],
})
export class EmployeeAttendanceComponent implements OnInit, OnDestroy {
  private readonly apiUrl = 'http://localhost:8080/api';
  private timerSubscription?: Subscription;

  loading = true;
  errorMessage = '';
  empleado?: EmpleadoResponse;
  boletas: BoletaPagoResponse[] = [];

  totpHash = '';
  qrCells: boolean[] = [];
  segundosRestantes = 30;
  generandoValidacion = false;
  mensajeQr = '';

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.cargarEmpleado();
    this.timerSubscription = interval(1000).subscribe(() => this.actualizarTotp());
  }

  ngOnDestroy(): void {
    this.timerSubscription?.unsubscribe();
  }

  cargarEmpleado(): void {
    const email = this.authService.getEmail();
    this.loading = true;
    this.errorMessage = '';

    this.http.get<EmpleadoResponse[]>(this.apiUrl + '/empleados').subscribe({
      next: (empleados) => {
        this.empleado = (empleados || []).find((item) => item.email === email) || undefined;
        this.loading = false;

        if (!this.empleado) {
          this.errorMessage = 'No se encontro la ficha del empleado asociada a esta cuenta.';
          return;
        }

        this.cargarBoletas();
        this.actualizarTotp(true);
      },
      error: () => {
        this.errorMessage = 'No se pudo cargar la informacion del empleado.';
        this.loading = false;
      },
    });
  }

  cargarBoletas(): void {
    this.http.get<BoletaPagoResponse[]>(this.apiUrl + '/boletas_pago').subscribe({
      next: (boletas) => {
        const empleadoId = Number(this.empleado?.id);
        this.boletas = (boletas || [])
          .filter((boleta) => Number(boleta.empleadoId) === empleadoId)
          .sort((a, b) => new Date(b.fechaEmision || 0).getTime() - new Date(a.fechaEmision || 0).getTime());
      },
      error: () => {
        this.boletas = [];
      },
    });
  }

  actualizarTotp(forzar = false): void {
    if (!this.empleado) {
      return;
    }

    const ventana = Math.floor(Date.now() / 30000);
    const restante = 30 - Math.floor((Date.now() % 30000) / 1000);
    const nuevoHash = this.crearTotpHash(Number(this.empleado.id), ventana);

    this.segundosRestantes = restante;

    if (forzar || nuevoHash !== this.totpHash) {
      this.totpHash = nuevoHash;
      this.qrCells = this.crearQrVisual(nuevoHash);
      this.registrarValidacionTotp(nuevoHash);
    }
  }

  descargarBoleta(boletaId: number): void {
    window.open(this.apiUrl + '/nomina/boleta/' + boletaId + '/pdf', '_blank');
  }

  nombreCompleto(): string {
    return [this.empleado?.nombres, this.empleado?.apellidos].filter(Boolean).join(' ') || 'Empleado';
  }

  private registrarValidacionTotp(hash: string): void {
    this.generandoValidacion = true;
    this.mensajeQr = '';

    this.http.post(this.apiUrl + '/validaciones-seguridad', {
      asistenciaId: null,
      dispositivoId: null,
      totpHash: hash,
      totpValido: true,
    }).subscribe({
      next: () => {
        this.mensajeQr = 'Codigo TOTP activo para registro de asistencia.';
        this.generandoValidacion = false;
      },
      error: () => {
        this.mensajeQr = 'El QR se genero, pero no se pudo registrar la validacion en el servidor.';
        this.generandoValidacion = false;
      },
    });
  }

  private crearTotpHash(empleadoId: number, ventana: number): string {
    const semilla = 'EMP-' + empleadoId + '-' + ventana;

    return btoa(semilla).replace(/=+$/g, '').slice(0, 18);
  }

  private crearQrVisual(hash: string): boolean[] {
    const base = Array.from(hash).reduce((total, char) => total + char.charCodeAt(0), 0);

    return Array.from({ length: 121 }, (_, index) => {
      const fila = Math.floor(index / 11);
      const columna = index % 11;
      const esquina = (fila < 4 && columna < 4) || (fila < 4 && columna > 6) || (fila > 6 && columna < 4);

      return esquina || ((base + index * 5 + fila * 7 + columna * 11) % 6 < 3);
    });
  }
}
