import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { AuthService } from '../../../../core/services/auth.service';
import { AsistenciaService } from '../../../../core/services/asistencia.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { CalendarioAnual, CalendarioDia, CalendarioMes, RegistroAsistencia } from '../../../../core/models/registro-asistencia.model';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';

@Component({
  selector: 'app-asistencia-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './asistencia-dashboard.component.html',
  styleUrls: ['./asistencia-dashboard.component.scss']
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
  viewMode = signal<'mensual' | 'anual'>('mensual');
  activeTab = signal<'calendario' | 'historial'>('calendario');
  month = signal<CalendarioMes | null>(null);
  annual = signal<CalendarioAnual | null>(null);
  empleados = signal<EmpleadoResponse[]>([]);
  historial = signal<RegistroAsistencia[]>([]);
  loadingHistorial = signal(false);

  // Asistencias de hoy (Admin/Supervisor)
  activeSubTab = signal<'hoy' | 'por_empleado'>('hoy');
  searchTerm = signal('');
  asistenciasHoyList = signal<RegistroAsistencia[]>([]);
  loadingHoy = signal(false);
  fechaConsultaHoy = (() => {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  })();

  // Marcado de salida
  yaMarcoEntrada = signal(false);
  yaMarcoSalida = signal(false);
  loadingMarcado = signal(false);

  manualPayload = '';
  selectedEmpleadoId?: number;
  selectedMonth = new Date().getMonth() + 1;
  selectedYear = new Date().getFullYear();
  editFecha = (() => {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  })();
  editEstado = 'VALIDADO';
  editObservaciones = '';
  private qrTimer?: number;
  private countdownInterval?: any;
  private scanner: any;

  weekDays = ['Lun','Mar','Mie','Jue','Vie','Sab','Dom'];
  months = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'].map((label, i) => ({ label, value: i + 1 }));

  constructor(
    private auth: AuthService,
    private asistencia: AsistenciaService,
    private empleadoService: EmpleadoService
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
      next: data => {
        const sorted = data.sort((a, b) => {
          const tA = a.fechaHora ? new Date(a.fechaHora).getTime() : 0;
          const tB = b.fechaHora ? new Date(b.fechaHora).getTime() : 0;
          return tB - tA;
        });
        this.asistenciasHoyList.set(sorted);
        this.loadingHoy.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'No se pudo cargar las asistencias de hoy');
        this.loadingHoy.set(false);
      }
    });
  }

  // ---- QR ----

  loadQr(): void {
    this.asistencia.miQr().subscribe({
      next: async qr => {
        this.qrName.set(qr.empleadoNombre);
        this.secondsLeft.set(qr.segundosRestantes);
        try {
          const QRCode = await import('qrcode');
          this.qrImage.set(await QRCode.toDataURL(qr.payload, { width: 235, margin: 1 }));
        } catch {
          this.qrImage.set('');
          this.error.set('No se pudo renderizar el QR.');
        }

        if (this.qrTimer) window.clearTimeout(this.qrTimer);
        if (this.countdownInterval) window.clearInterval(this.countdownInterval);

        this.countdownInterval = window.setInterval(() => {
          this.secondsLeft.update(s => {
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
      error: err => this.error.set(err.error?.message || err.error?.error || 'No se pudo cargar el QR')
    });
  }

  // ---- Marcado de salida (empleado) ----

  checkMarcadoHoy(): void {
    // Verificar si ya marco entrada y/o salida hoy utilizando fecha local del navegador
    this.asistencia.miHistorial().subscribe({
      next: (registros) => {
        const d = new Date();
        const hoy = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
        const hoyRegistros = registros.filter(r => r.fechaHora?.startsWith(hoy));
        this.yaMarcoEntrada.set(hoyRegistros.some(r => r.tipoMarcacion === 'ENTRADA'));
        this.yaMarcoSalida.set(hoyRegistros.some(r => r.tipoMarcacion === 'SALIDA'));
      }
    });
  }

  // ---- Employees (admin) ----

  loadEmployees(): void {
    this.empleadoService.listarActivos().subscribe({
      next: data => {
        this.empleados.set(data);
        this.selectedEmpleadoId = this.selectedEmpleadoId || data[0]?.id;
        this.loadCalendar();
      },
      error: err => this.error.set(err.error?.message || 'No se pudo cargar empleados')
    });
  }

  // ---- Calendario ----

  loadCalendar(): void {
    if (!this.isEmployee && !this.selectedEmpleadoId) return;
    const req: Observable<CalendarioMes | CalendarioAnual> = this.viewMode() === 'mensual'
      ? (this.isEmployee ? this.asistencia.miCalendario(this.selectedYear, this.selectedMonth) : this.asistencia.calendarioEmpleado(this.selectedEmpleadoId!, this.selectedYear, this.selectedMonth))
      : (this.isEmployee ? this.asistencia.miCalendarioAnual(this.selectedYear) : this.asistencia.calendarioAnualEmpleado(this.selectedEmpleadoId!, this.selectedYear));
    req.subscribe({
      next: data => Array.isArray((data as CalendarioAnual).meses) ? this.annual.set(data as CalendarioAnual) : this.month.set(data as CalendarioMes),
      error: err => this.error.set(err.error?.message || err.error?.error || 'No se pudo cargar el calendario')
    });
  }

  monthDays(): CalendarioDia[] { return this.month()?.dias || []; }

  leadingBlanks(): number[] {
    const first = this.monthDays()[0]?.fecha;
    if (!first) return [];
    const day = new Date(first + 'T00:00:00').getDay();
    return Array(day === 0 ? 6 : day - 1).fill(0);
  }

  monthName(mes: number): string { return this.months[mes - 1]?.label || ''; }

  selectDay(day: CalendarioDia): void {
    if (!this.isAdmin) return; // Solo admin puede editar
    this.editFecha = day.fecha;
    this.editEstado = day.estado === 'ASISTIO' ? 'VALIDADO' : this.editEstado;
  }

  // ---- Historial ----

  loadHistorial(): void {
    this.loadingHistorial.set(true);
    const req = this.isEmployee
      ? this.asistencia.miHistorial()
      : (this.selectedEmpleadoId ? this.asistencia.historialEmpleado(this.selectedEmpleadoId) : this.asistencia.miHistorial());
    req.subscribe({
      next: data => {
        this.historial.set(data);
        this.loadingHistorial.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'No se pudo cargar historial');
        this.loadingHistorial.set(false);
      }
    });
  }

  // ---- Scanner ----

  async startScanner(): Promise<void> {
    this.error.set('');
    try {
      const mod = await import('html5-qrcode');
      this.scanner = new mod.Html5Qrcode('qr-reader');
      await this.scanner.start({ facingMode: 'environment' }, { fps: 10, qrbox: 220 }, (text: string) => this.handleScan(text));
      this.scannerOn.set(true);
    } catch {
      this.error.set('No se pudo iniciar la camara. Puedes pegar el QR manualmente.');
    }
  }

  async stopScanner(): Promise<void> {
    if (this.scanner) {
      try { await this.scanner.stop(); await this.scanner.clear(); } catch {}
      this.scanner = null;
    }
    this.scannerOn.set(false);
  }

  scanManual(): void { this.handleScan(this.manualPayload); }

  handleScan(payload: string): void {
    if (!payload) return;
    this.asistencia.scanQr(payload).subscribe({
      next: r => {
        const tipo = r.tipoMarcacion || 'asistencia';
        this.message.set(`${tipo} registrada para empleado #${r.empleadoId}`);
        this.error.set('');
        this.manualPayload = '';
        this.loadCalendar();
        this.checkMarcadoHoy();
        if (!this.isEmployee) {
          this.loadAsistenciasHoy();
        }
        this.stopScanner();
      },
      error: err => this.error.set(err.error?.message || err.error?.error || 'No se pudo registrar la asistencia')
    });
  }

  // ---- Edición manual (SOLO ADMIN) ----

  saveAttendance(): void {
    if (!this.isAdmin) {
      this.error.set('Solo los administradores pueden editar asistencias.');
      return;
    }
    if (!this.selectedEmpleadoId) return;
    const existing = this.monthDays().find(d => d.fecha === this.editFecha && d.asistenciaId);
    const payload: RegistroAsistencia = {
      empleadoId: this.selectedEmpleadoId,
      fechaHora: `${this.editFecha}T09:00:00`,
      tipoMarcacion: 'ENTRADA',
      metodo: 'MANUAL',
      estado: this.editEstado,
      observaciones: this.editObservaciones
    };
    const req = existing?.asistenciaId ? this.asistencia.actualizar(existing.asistenciaId, payload) : this.asistencia.crear(payload);
    req.subscribe({
      next: () => { this.message.set('Asistencia guardada'); this.error.set(''); this.loadCalendar(); },
      error: err => this.error.set(err.error?.message || err.error?.error || 'No se pudo guardar')
    });
  }

  // Admin: marcar entrada/salida manual para un empleado
  marcarEntradaAdmin(): void {
    if (!this.isAdmin || !this.selectedEmpleadoId) return;
    this.loadingMarcado.set(true);
    this.asistencia.registrarEntrada(this.selectedEmpleadoId, 'MANUAL').subscribe({
      next: () => {
        this.message.set('Entrada registrada correctamente');
        this.error.set('');
        this.loadingMarcado.set(false);
        this.loadCalendar();
      },
      error: err => {
        this.error.set(err.error?.message || 'Error al registrar entrada');
        this.loadingMarcado.set(false);
      }
    });
  }

  marcarSalidaAdmin(): void {
    if (!this.isAdmin || !this.selectedEmpleadoId) return;
    this.loadingMarcado.set(true);
    this.asistencia.registrarSalida(this.selectedEmpleadoId, 'MANUAL').subscribe({
      next: () => {
        this.message.set('Salida registrada correctamente');
        this.error.set('');
        this.loadingMarcado.set(false);
        this.loadCalendar();
      },
      error: err => {
        this.error.set(err.error?.message || 'Error al registrar salida');
        this.loadingMarcado.set(false);
      }
    });
  }

  getEmpleadoNombre(id?: number): string {
    if (!id) return '—';
    const emp = this.empleados().find(e => e.id === id);
    return emp ? `${emp.apellidos}, ${emp.nombres}` : `Empleado #${id}`;
  }

  getEmpleadoDni(id?: number): string {
    if (!id) return '—';
    const emp = this.empleados().find(e => e.id === id);
    return emp ? emp.dni : '—';
  }

  getFilteredAsistenciasHoy(): any[] {
    const term = this.searchTerm().toLowerCase().trim();
    const allRegistered = this.asistenciasHoyList();
    const emps = this.empleados();
    const selectedDateStr = this.fechaConsultaHoy;

    const todayStr = (() => {
      const d = new Date();
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    })();

    const isPastDay = selectedDateStr < todayStr;

    const resultList: any[] = [];

    for (const emp of emps) {
      const empRecords = allRegistered.filter(r => r.empleadoId === emp.id);
      const entradaRecords = empRecords.filter(r => r.tipoMarcacion === 'ENTRADA');
      const salidaRecords = empRecords.filter(r => r.tipoMarcacion === 'SALIDA');

      // 1. ENTRADA
      if (entradaRecords.length > 0) {
        resultList.push(...entradaRecords);
      } else {
        resultList.push({
          id: -emp.id * 10 - 1, // virtual unique id
          isVirtual: true,
          empleadoId: emp.id,
          tipoMarcacion: isPastDay ? 'No registrado' : 'Pendiente',
          fechaHora: null,
          metodo: '—',
          estado: isPastDay ? 'INVALIDO' : 'PENDIENTE',
          observaciones: '—'
        });
      }

      // 2. SALIDA
      if (salidaRecords.length > 0) {
        resultList.push(...salidaRecords);
      } else {
        resultList.push({
          id: -emp.id * 10 - 2, // virtual unique id
          isVirtual: true,
          empleadoId: emp.id,
          tipoMarcacion: isPastDay ? 'No registrado' : 'Pendiente',
          fechaHora: null,
          metodo: '—',
          estado: isPastDay ? 'INVALIDO' : 'PENDIENTE',
          observaciones: '—'
        });
      }
    }

    if (!term) return resultList;
    return resultList.filter(r => {
      const nombre = this.getEmpleadoNombre(r.empleadoId).toLowerCase();
      const dni = this.getEmpleadoDni(r.empleadoId).toLowerCase();
      return nombre.includes(term) || dni.includes(term);
    });
  }
}