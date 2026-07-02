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
  qrImage = signal('');
  qrName = signal('');
  secondsLeft = signal(0);
  scannerOn = signal(false);
  message = signal('');
  error = signal('');
  viewMode = signal<'mensual' | 'anual'>('mensual');
  month = signal<CalendarioMes | null>(null);
  annual = signal<CalendarioAnual | null>(null);
  empleados = signal<EmpleadoResponse[]>([]);

  manualPayload = '';
  selectedEmpleadoId?: number;
  selectedMonth = new Date().getMonth() + 1;
  selectedYear = new Date().getFullYear();
  editFecha = new Date().toISOString().slice(0, 10);
  editEstado = 'VALIDADO';
  editObservaciones = '';
  private qrTimer?: number;
  private countdownInterval?: any;
  private scanner: any;

  weekDays = ['Lun','Mar','Mie','Jue','Vie','Sab','Dom'];
  months = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'].map((label, i) => ({ label, value: i + 1 }));

  constructor(private auth: AuthService, private asistencia: AsistenciaService, private empleadoService: EmpleadoService) {}

  ngOnInit(): void {
    const role = this.auth.getCurrentUser()?.rol;
    this.isEmployee = role === 'EMPLEADO' || role === 'TRABAJADOR';
    if (this.isEmployee) this.loadQr();
    else this.loadEmployees();
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
          this.error.set('No se pudo renderizar el QR. Revisa que la dependencia qrcode esta instalada.');
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
      },
      error: err => this.error.set(err.error?.message || err.error?.error || 'No se pudo cargar el QR')
    });
  }

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
    if (this.isEmployee) return;
    this.editFecha = day.fecha;
    this.editEstado = day.estado === 'ASISTIO' ? 'VALIDADO' : this.editEstado;
  }

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
        this.message.set(`Asistencia registrada para empleado #${r.empleadoId}`);
        this.error.set('');
        this.manualPayload = '';
        this.loadCalendar();
        this.stopScanner();
      },
      error: err => this.error.set(err.error?.message || err.error?.error || 'No se pudo registrar la asistencia')
    });
  }

  saveAttendance(): void {
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
}