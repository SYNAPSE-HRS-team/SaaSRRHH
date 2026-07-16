import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Observable, forkJoin } from 'rxjs'; // Importamos forkJoin de forma estática

// 🔥 IMPORTS ESTÁTICOS CORREGIDOS
import { Html5Qrcode } from 'html5-qrcode';
import * as QRCode from 'qrcode';

import { EmpleadoResponse } from '../../../../core/models/empleado.model';
import {
  CalendarioAnual,
  CalendarioDia,
  CalendarioMes,
  RegistroAsistencia,
} from '../../../../core/models/registro-asistencia.model';
import { AsistenciaService } from '../../../../core/services/asistencia.service';
import { AuthService } from '../../../../core/services/auth.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';

function getLocalDateString(d: Date = new Date()): string {
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

@Component({
  selector: 'app-asistencia-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, MatIconModule],
  templateUrl: './asistencia-dashboard.component.html',
  styleUrls: ['./asistencia-dashboard.component.scss'],
})
export class AsistenciaDashboardComponent implements OnInit, OnDestroy {
  isEmployee = false;
  isAdmin = false;
  qrImage = signal('');
  qrName = signal('');
  secondsLeft = signal(0);
  scannerOn = signal(false);
  message = signal('');
  error = signal('');
  isProcessingScan = signal(false);
  viewMode = signal<'mensual' | 'anual'>('mensual');
  activeTab = signal<'calendario' | 'historial'>('calendario');
  month = signal<CalendarioMes | null>(null);
  annual = signal<CalendarioAnual | null>(null);
  empleados = signal<EmpleadoResponse[]>([]);
  historial = signal<RegistroAsistencia[]>([]);
  loadingHistorial = signal(false);
  activeSubTab = signal<'hoy' | 'por_empleado'>('hoy');
  searchTerm = signal('');
  asistenciasHoyList = signal<RegistroAsistencia[]>([]);
  loadingHoy = signal(false);
  fechaConsultaHoy = getLocalDateString();
  yaMarcoEntrada = signal(false);
  yaMarcoSalida = signal(false);
  loadingMarcado = signal(false);
  showDetailModal = signal(false);
  selectedEmployeeNameForModal = '';
  selectedEmployeeIdForModal?: number;
  selectedDateForModal = '';
  modalEntradaId?: number;
  modalEntradaHora = '';
  modalEntradaEstado = 'VALIDADO';
  modalEntradaObs = '';
  modalEntradaExiste = false;
  modalSalidaId?: number;
  modalSalidaHora = '';
  modalSalidaEstado = 'VALIDADO';
  modalSalidaObs = '';
  modalSalidaExiste = false;
  manualPayload = '';
  selectedEmpleadoId?: number;
  selectedMonth = new Date().getMonth() + 1;
  selectedYear = new Date().getFullYear();
  editFecha = getLocalDateString();
  editEstado = 'VALIDADO';
  editObservaciones = '';
  private qrTimer?: number;
  private countdownInterval?: any;
  private scanner: any = null;
  weekDays = ['Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab', 'Dom'];
  months = [
    'Enero',
    'Febrero',
    'Marzo',
    'Abril',
    'Mayo',
    'Junio',
    'Julio',
    'Agosto',
    'Septiembre',
    'Octubre',
    'Noviembre',
    'Diciembre',
  ].map((label, i) => ({ label, value: i + 1 }));

  constructor(
    private auth: AuthService,
    private asistencia: AsistenciaService,
    private empleadoService: EmpleadoService,
  ) {}

  ngOnInit(): void {
    const role = this.auth.getCurrentUser()?.rol;
    this.isEmployee = role === 'EMPLEADO' || role === 'TRABAJADOR';
    this.isAdmin = role === 'ADMIN';
    if (this.isEmployee) {
      this.loadQr();
    } else {
      this.loadEmployees();
    }
    this.loadCalendar();
  }

  ngOnDestroy(): void {
    if (this.qrTimer) window.clearTimeout(this.qrTimer);
    if (this.countdownInterval) window.clearInterval(this.countdownInterval);
    this.stopScanner();
  }

  get fechaMaxima(): string {
    return getLocalDateString();
  }
  esFechaHoy(): boolean {
    return this.selectedDateForModal === this.fechaMaxima;
  }

  setView(mode: 'mensual' | 'anual'): void {
    this.viewMode.set(mode);
    this.loadCalendar();
  }
  setTab(tab: 'calendario' | 'historial'): void {
    this.activeTab.set(tab);
    if (tab === 'historial') {
      if (!this.isEmployee && this.activeSubTab() === 'hoy') {
        this.loadAsistenciasHoy();
      } else {
        this.loadHistorial();
      }
    }
  }
  setSubTab(subTab: 'hoy' | 'por_empleado'): void {
    this.activeSubTab.set(subTab);
    if (subTab === 'hoy') {
      this.loadAsistenciasHoy();
    } else {
      this.loadHistorial();
    }
  }

  loadAsistenciasHoy(): void {
    this.loadingHoy.set(true);
    this.asistencia.asistenciasHoy(this.fechaConsultaHoy).subscribe({
      next: (data) => {
        this.asistenciasHoyList.set(
          data.sort(
            (a, b) =>
              (b.fechaHora ? new Date(b.fechaHora).getTime() : 0) -
              (a.fechaHora ? new Date(a.fechaHora).getTime() : 0),
          ),
        );
        this.loadingHoy.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'No se pudo cargar las asistencias de hoy');
        this.loadingHoy.set(false);
      },
    });
  }

  loadQr(): void {
    this.asistencia.miQr().subscribe({
      next: async (qr) => {
        this.qrName.set(qr.empleadoNombre);
        this.secondsLeft.set(qr.segundosRestantes);
        try {
          // 🔥 Uso directo de la librería importada de forma estática
          const qrDataUrl = await QRCode.toDataURL(qr.payload, { width: 235, margin: 1 });
          this.qrImage.set(qrDataUrl);
        } catch (e) {
          this.qrImage.set('');
        }
        if (this.qrTimer) window.clearTimeout(this.qrTimer);
        if (this.countdownInterval) window.clearInterval(this.countdownInterval);
        this.countdownInterval = window.setInterval(() => {
          this.secondsLeft.update((s) => {
            if (s <= 1) {
              window.clearInterval(this.countdownInterval);
              this.loadQr();
              return 0;
            }
            return s - 1;
          });
        }, 1000);
        this.checkMarcadoHoy();
      },
      error: (err) => this.error.set(err.error?.message || 'No se pudo cargar el QR'),
    });
  }

  checkMarcadoHoy(): void {
    this.asistencia.miHistorial().subscribe({
      next: (registros) => {
        const hoy = getLocalDateString();
        const hoyRegistros = registros.filter((r) => r.fechaHora?.startsWith(hoy));
        this.yaMarcoEntrada.set(hoyRegistros.some((r) => r.tipoMarcacion === 'ENTRADA'));
        this.yaMarcoSalida.set(hoyRegistros.some((r) => r.tipoMarcacion === 'SALIDA'));
      },
    });
  }

  loadEmployees(): void {
    this.empleadoService.listarActivos().subscribe({
      next: (data) => {
        this.empleados.set(data);
        this.selectedEmpleadoId = this.selectedEmpleadoId || data[0]?.id;
        this.loadCalendar();
      },
      error: (err) => this.error.set(err.error?.message || 'No se pudo cargar empleados'),
    });
  }

  loadCalendar(): void {
    if (!this.isEmployee && !this.selectedEmpleadoId) return;
    const req: Observable<CalendarioMes | CalendarioAnual> =
      this.viewMode() === 'mensual'
        ? this.isEmployee
          ? this.asistencia.miCalendario(this.selectedYear, this.selectedMonth)
          : this.asistencia.calendarioEmpleado(
              this.selectedEmpleadoId!,
              this.selectedYear,
              this.selectedMonth,
            )
        : this.isEmployee
          ? this.asistencia.miCalendarioAnual(this.selectedYear)
          : this.asistencia.calendarioAnualEmpleado(this.selectedEmpleadoId!, this.selectedYear);
    req.subscribe({
      next: (data) =>
        Array.isArray((data as CalendarioAnual).meses)
          ? this.annual.set(data as CalendarioAnual)
          : this.month.set(data as CalendarioMes),
      error: (err) => this.error.set(err.error?.message || 'No se pudo cargar el calendario'),
    });
  }

  monthDays(): CalendarioDia[] {
    return this.month()?.dias || [];
  }
  leadingBlanks(): number[] {
    const first = this.monthDays()[0]?.fecha;
    if (!first) return [];
    const day = new Date(first + 'T00:00:00').getDay();
    return Array(day === 0 ? 6 : day - 1).fill(0);
  }
  monthName(mes: number): string {
    return this.months[mes - 1]?.label || '';
  }
  selectDay(day: CalendarioDia): void {
    if (!this.isAdmin) return;
    this.editFecha = day.fecha;
  }

  loadHistorial(): void {
    this.loadingHistorial.set(true);
    const req = this.isEmployee
      ? this.asistencia.miHistorial()
      : this.selectedEmpleadoId
        ? this.asistencia.historialEmpleado(this.selectedEmpleadoId)
        : this.asistencia.miHistorial();
    req.subscribe({
      next: (data) => {
        this.historial.set(data);
        this.loadingHistorial.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'No se pudo cargar historial');
        this.loadingHistorial.set(false);
      },
    });
  }

  // 🔥 Método optimizado utilizando la librería instanciada estáticamente
  async startScanner(): Promise<void> {
    this.error.set('');
    try {
      this.scanner = new Html5Qrcode('qr-reader');
      await this.scanner.start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: 220 },
        (text: string) => this.handleScan(text),
      );
      this.scannerOn.set(true);
    } catch (err) {
      this.error.set(
        'No se pudo iniciar la cámara. Asegúrate de otorgar permisos y de usar un entorno seguro (HTTPS/localhost).',
      );
    }
  }

  async stopScanner(): Promise<void> {
    if (this.scanner) {
      try {
        await this.scanner.stop();
      } catch {}
      this.scanner = null;
    }
    this.scannerOn.set(false);
  }

  scanManual(): void {
    this.handleScan(this.manualPayload);
  }

  handleScan(payload: string): void {
    if (!payload || this.isProcessingScan()) return;
    this.isProcessingScan.set(true);
    this.asistencia.scanQr(payload).subscribe({
      next: (r) => {
        this.message.set(`${r.tipoMarcacion || 'asistencia'} registrada`);
        this.error.set('');
        this.manualPayload = '';
        if (!this.isEmployee && r.empleadoId) {
          this.selectedEmpleadoId = r.empleadoId;
        }
        this.loadCalendar();
        this.checkMarcadoHoy();
        if (!this.isEmployee) {
          this.loadAsistenciasHoy();
        }
        this.stopScanner();
        this.isProcessingScan.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'No se pudo registrar');
        this.isProcessingScan.set(false);
      },
    });
  }

  saveAttendance(): void {
    if (!this.isAdmin || !this.selectedEmpleadoId) return;
    const payload: RegistroAsistencia = {
      empleadoId: this.selectedEmpleadoId,
      fechaHora: `${this.editFecha}T09:00:00`,
      tipoMarcacion: 'ENTRADA',
      metodo: 'MANUAL',
      estado: this.editEstado,
      observaciones: this.editObservaciones,
    };
    const existing = this.monthDays().find((d) => d.fecha === this.editFecha && d.asistenciaId);
    const req = existing?.asistenciaId
      ? this.asistencia.actualizar(existing.asistenciaId, payload)
      : this.asistencia.crear(payload);
    req.subscribe({
      next: () => {
        this.message.set('Asistencia guardada');
        this.error.set('');
        this.loadCalendar();
      },
      error: (err) => this.error.set(err.error?.message || 'No se pudo guardar'),
    });
  }

  marcarEntradaAdmin(): void {
    if (!this.isAdmin || !this.selectedEmpleadoId) return;
    this.loadingMarcado.set(true);
    this.asistencia.registrarEntrada(this.selectedEmpleadoId, 'MANUAL').subscribe({
      next: () => {
        this.message.set('Entrada registrada');
        this.loadingMarcado.set(false);
        this.loadCalendar();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error');
        this.loadingMarcado.set(false);
      },
    });
  }
  marcarSalidaAdmin(): void {
    if (!this.isAdmin || !this.selectedEmpleadoId) return;
    this.loadingMarcado.set(true);
    this.asistencia.registrarSalida(this.selectedEmpleadoId, 'MANUAL').subscribe({
      next: () => {
        this.message.set('Salida registrada');
        this.loadingMarcado.set(false);
        this.loadCalendar();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error');
        this.loadingMarcado.set(false);
      },
    });
  }

  getEmpleadoNombre(id?: number): string {
    if (!id) return '—';
    const emp = this.empleados().find((e) => e.id === id);
    return emp ? `${emp.apellidos}, ${emp.nombres}` : `Empleado #${id}`;
  }
  getEmpleadoDni(id?: number): string {
    if (!id) return '—';
    const emp = this.empleados().find((e) => e.id === id);
    return emp ? emp.dni : '—';
  }

  getFilteredAsistenciasHoy(): any[] {
    const term = this.searchTerm().toLowerCase().trim();
    const allRegistered = this.asistenciasHoyList();
    const emps = this.empleados();
    const todayStr = getLocalDateString();
    const isPastDay = this.fechaConsultaHoy < todayStr;
    const resultList: any[] = [];
    for (const emp of emps) {
      const empRecords = allRegistered.filter((r) => r.empleadoId === emp.id);
      const entrada = empRecords.find((r) => r.tipoMarcacion === 'ENTRADA');
      const salida = empRecords.find((r) => r.tipoMarcacion === 'SALIDA');
      resultList.push({
        id: emp.id,
        empleadoId: emp.id,
        dni: emp.dni,
        nombre: `${emp.apellidos}, ${emp.nombres}`,
        entrada: entrada || null,
        salida: salida || null,
        estado:
          entrada && salida
            ? entrada.estado
            : entrada
              ? entrada.estado
              : salida
                ? salida.estado
                : isPastDay
                  ? 'INVALIDO'
                  : 'PENDIENTE',
        isPastDay,
      });
    }
    if (!term) return resultList;
    return resultList.filter(
      (r) => r.nombre.toLowerCase().includes(term) || r.dni.toLowerCase().includes(term),
    );
  }

  crearAsistencia(row: any): void {
    if (!this.isAdmin) return;
    this.selectedEmployeeIdForModal = row.empleadoId;
    this.selectedEmployeeNameForModal = row.nombre;
    this.selectedDateForModal = this.fechaConsultaHoy;
    this.modalEntradaId = undefined;
    this.modalEntradaHora = '';
    this.modalEntradaEstado = 'VALIDADO';
    this.modalEntradaObs = '';
    this.modalEntradaExiste = false;
    this.modalSalidaId = undefined;
    this.modalSalidaHora = '';
    this.modalSalidaEstado = 'VALIDADO';
    this.modalSalidaObs = '';
    this.modalSalidaExiste = false;
    this.showDetailModal.set(true);
  }

  openAsistenciaModal(row: any): void {
    this.selectedEmployeeIdForModal = row.empleadoId;
    this.selectedEmployeeNameForModal = row.nombre;
    this.selectedDateForModal = this.fechaConsultaHoy;
    this.modalEntradaId = undefined;
    this.modalEntradaHora = '';
    this.modalEntradaEstado = 'VALIDADO';
    this.modalEntradaObs = '';
    this.modalEntradaExiste = false;
    this.modalSalidaId = undefined;
    this.modalSalidaHora = '';
    this.modalSalidaEstado = 'VALIDADO';
    this.modalSalidaObs = '';
    this.modalSalidaExiste = false;
    if (row.entrada) {
      this.modalEntradaId = row.entrada.id;
      this.modalEntradaHora = row.entrada.fechaHora ? row.entrada.fechaHora.slice(11, 16) : '';
      this.modalEntradaEstado = row.entrada.estado;
      this.modalEntradaObs = row.entrada.observaciones || '';
      this.modalEntradaExiste = true;
    }
    if (row.salida) {
      this.modalSalidaId = row.salida.id;
      this.modalSalidaHora = row.salida.fechaHora ? row.salida.fechaHora.slice(11, 16) : '';
      this.modalSalidaEstado = row.salida.estado;
      this.modalSalidaObs = row.salida.observaciones || '';
      this.modalSalidaExiste = true;
    }
    this.showDetailModal.set(true);
  }

  saveModalAsistencia(): void {
    if (!this.isAdmin || !this.selectedEmployeeIdForModal) return;
    const promises: Observable<any>[] = [];
    if (this.modalEntradaHora) {
      const payload: RegistroAsistencia = {
        empleadoId: this.selectedEmployeeIdForModal,
        fechaHora: `${this.selectedDateForModal}T${this.modalEntradaHora}:00`,
        tipoMarcacion: 'ENTRADA',
        metodo: 'MANUAL',
        estado: this.modalEntradaEstado,
        observaciones: this.modalEntradaObs,
      };
      promises.push(
        this.modalEntradaId
          ? this.asistencia.actualizar(this.modalEntradaId, payload)
          : this.asistencia.crear(payload),
      );
    }
    if (this.modalSalidaHora) {
      const payload: RegistroAsistencia = {
        empleadoId: this.selectedEmployeeIdForModal,
        fechaHora: `${this.selectedDateForModal}T${this.modalSalidaHora}:00`,
        tipoMarcacion: 'SALIDA',
        metodo: 'MANUAL',
        estado: this.modalSalidaEstado,
        observaciones: this.modalSalidaObs,
      };
      promises.push(
        this.modalSalidaId
          ? this.asistencia.actualizar(this.modalSalidaId, payload)
          : this.asistencia.crear(payload),
      );
    }
    if (promises.length === 0) {
      this.showDetailModal.set(false);
      return;
    }

    // 🔥 Uso directo de rxjs (forkJoin) importado al principio del archivo
    forkJoin(promises).subscribe({
      next: () => {
        this.message.set('✅ Asistencias guardadas');
        this.error.set('');
        this.showDetailModal.set(false);
        this.loadCalendar();
        this.loadAsistenciasHoy();
      },
      error: (err) => {
        this.error.set('❌ ' + (err.error?.message || 'Error al guardar'));
      },
    });
  }

  hasErrorEstados(): boolean {
    return (
      this.modalEntradaEstado === 'RECHAZADO' ||
      this.modalSalidaEstado === 'RECHAZADO' ||
      this.modalEntradaEstado === 'OBSERVADO' ||
      this.modalSalidaEstado === 'OBSERVADO'
    );
  }

  getResumenDia(entradaEstado: string, salidaEstado: string): string {
    if (entradaEstado === 'RECHAZADO' || salidaEstado === 'RECHAZADO') {
      return 'Rechazado';
    }
    if (entradaEstado === 'OBSERVADO' || salidaEstado === 'OBSERVADO') {
      return 'Observado';
    }
    if (entradaEstado === 'VALIDADO' || salidaEstado === 'VALIDADO') {
      return 'Validado';
    }
    return 'Pendiente';
  }
}
