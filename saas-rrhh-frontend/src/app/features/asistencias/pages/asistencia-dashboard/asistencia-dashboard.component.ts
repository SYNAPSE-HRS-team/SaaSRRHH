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
  template: `
    <div class="attendance-page">
      <div class="page-header">
        <div>
          <h1>Asistencia</h1>
          <p>{{ isEmployee ? 'Muestra tu QR y revisa tu calendario' : 'Escanea QR, registra y edita asistencias' }}</p>
        </div>
        <div class="view-toggle">
          <button [class.active]="viewMode() === 'mensual'" (click)="setView('mensual')">Mensual</button>
          <button [class.active]="viewMode() === 'anual'" (click)="setView('anual')">Anual</button>
        </div>
      </div>

      @if (message()) { <div class="notice success">{{ message() }}</div> }
      @if (error()) { <div class="notice error">{{ error() }}</div> }

      <section class="top-grid">
        @if (isEmployee) {
          <div class="panel qr-panel">
            <h2>Mi QR dinamico</h2>
            <div class="qr-box">
              @if (qrImage()) { <img [src]="qrImage()" alt="QR de asistencia" /> }
              @else { <div class="qr-placeholder">QR</div> }
            </div>
            <div class="qr-meta">
              <strong>{{ qrName() }}</strong>
              <span>Se renueva en {{ secondsLeft() }}s</span>
            </div>
            <button class="primary" (click)="loadQr()">Actualizar QR</button>
          </div>
        } @else {
          <div class="panel scanner-panel">
            <h2>Escanear QR</h2>
            <div id="qr-reader" class="reader"></div>
            <textarea [(ngModel)]="manualPayload" placeholder="Pega aqu? el QR si la camara no esta disponible"></textarea>
            <div class="actions">
              <button class="primary" (click)="startScanner()" [disabled]="scannerOn()">Iniciar camara</button>
              <button class="ghost" (click)="stopScanner()" [disabled]="!scannerOn()">Detener</button>
              <button class="primary" (click)="scanManual()">Registrar</button>
            </div>
          </div>
        }

        @if (!isEmployee) {
          <div class="panel editor-panel">
            <h2>Editar asistencia</h2>
            <label>Empleado</label>
            <select [(ngModel)]="selectedEmpleadoId" (change)="loadCalendar()">
              @for (emp of empleados(); track emp.id) {
                <option [ngValue]="emp.id">{{ emp.apellidos }}, {{ emp.nombres }}</option>
              }
            </select>
            <label>Fecha</label>
            <input type="date" [(ngModel)]="editFecha" />
            <label>Estado</label>
            <select [(ngModel)]="editEstado">
              <option value="VALIDADO">VALIDADO</option>
              <option value="OBSERVADO">OBSERVADO</option>
              <option value="RECHAZADO">RECHAZADO</option>
            </select>
            <label>Observaciones</label>
            <textarea [(ngModel)]="editObservaciones"></textarea>
            <button class="primary" (click)="saveAttendance()">Guardar asistencia</button>
          </div>
        }
      </section>

      <section class="panel calendar-panel">
        <div class="calendar-toolbar">
          <h2>Calendario</h2>
          <div class="date-controls">
            @if (viewMode() === 'mensual') {
              <select [(ngModel)]="selectedMonth" (change)="loadCalendar()">
                @for (m of months; track m.value) { <option [ngValue]="m.value">{{ m.label }}</option> }
              </select>
            }
            <input type="number" [(ngModel)]="selectedYear" (change)="loadCalendar()" min="2020" max="2100" />
          </div>
        </div>

        @if (viewMode() === 'mensual') {
          <div class="month-grid">
            @for (name of weekDays; track name) { <div class="weekday">{{ name }}</div> }
            @for (blank of leadingBlanks(); track $index) { <div class="day empty"></div> }
            @for (day of monthDays(); track day.fecha) {
              <button class="day" [class.present]="day.estado === 'ASISTIO'" [class.absent]="day.estado === 'FALTA'" (click)="selectDay(day)">
                <span>{{ day.fecha | date:'d' }}</span>
                @if (day.horaEntrada) { <small>{{ day.horaEntrada.slice(0,5) }}</small> }
              </button>
            }
          </div>
        } @else {
          <div class="year-grid">
            @for (month of annual()?.meses; track month.mes) {
              <div class="mini-month">
                <h3>{{ monthName(month.mes) }}</h3>
                <div class="mini-days">
                  @for (day of month.dias; track day.fecha) {
                    <span [class.present]="day.estado === 'ASISTIO'" [class.absent]="day.estado === 'FALTA'"></span>
                  }
                </div>
              </div>
            }
          </div>
        }
      </section>
    </div>
  `,
  styles: [`
    .attendance-page{max-width:1280px;margin:0 auto;color:#172033}.page-header{display:flex;justify-content:space-between;gap:1rem;align-items:flex-start;margin-bottom:1rem}.page-header h1{margin:0;font-size:1.75rem}.page-header p{margin:.25rem 0 0;color:#607086}.view-toggle{display:flex;border:1px solid #d9e1ec;border-radius:8px;overflow:hidden}.view-toggle button{border:0;background:#fff;padding:.65rem 1rem;cursor:pointer}.view-toggle .active{background:#1f6feb;color:white}.notice{padding:.75rem 1rem;border-radius:8px;margin-bottom:1rem}.success{background:#e9f9ef;color:#187245}.error{background:#fff0f0;color:#b42318}.top-grid{display:grid;grid-template-columns:1fr 1fr;gap:1rem;margin-bottom:1rem}.panel{background:white;border:1px solid #e3e8f0;border-radius:8px;padding:1rem;box-shadow:0 2px 10px rgba(16,24,40,.04)}.panel h2{font-size:1.05rem;margin:0 0 1rem}.qr-panel{display:flex;flex-direction:column;align-items:center}.qr-box{width:260px;aspect-ratio:1;border:1px solid #e3e8f0;border-radius:8px;display:grid;place-items:center;background:#fff}.qr-box img{width:235px;height:235px}.qr-placeholder{font-size:2rem;color:#9aa7b7}.qr-meta{display:flex;flex-direction:column;align-items:center;gap:.25rem;margin:1rem 0;color:#607086}.qr-meta strong{color:#172033}.reader{min-height:260px;border:1px dashed #b8c4d6;border-radius:8px;overflow:hidden;background:#f8fafc}label{font-size:.8rem;font-weight:700;color:#607086;margin-top:.75rem;display:block}select,input,textarea{width:100%;box-sizing:border-box;border:1px solid #ced7e3;border-radius:8px;padding:.65rem;margin-top:.35rem;font:inherit}textarea{min-height:70px;resize:vertical}.actions{display:flex;flex-wrap:wrap;gap:.5rem;margin-top:.75rem}.primary,.ghost{border:0;border-radius:8px;padding:.7rem 1rem;font-weight:700;cursor:pointer}.primary{background:#1f6feb;color:white}.ghost{background:#eef2f7;color:#172033}.primary:disabled,.ghost:disabled{opacity:.55;cursor:not-allowed}.calendar-toolbar{display:flex;justify-content:space-between;gap:1rem;align-items:center}.date-controls{display:flex;gap:.5rem}.date-controls input{max-width:110px}.month-grid{display:grid;grid-template-columns:repeat(7,minmax(0,1fr));gap:.35rem;margin-top:1rem}.weekday{text-align:center;font-size:.75rem;font-weight:800;color:#607086}.day{min-height:82px;border:1px solid #e3e8f0;border-radius:8px;background:#f8fafc;text-align:left;padding:.5rem;display:flex;flex-direction:column;justify-content:space-between}.day.empty{background:transparent;border:0}.day.present,.mini-days .present{background:#dff7e8;border-color:#7dd99e;color:#116b39}.day.absent,.mini-days .absent{background:#ffe4e2;border-color:#f5a29b;color:#9f1f17}.day small{font-size:.72rem}.year-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:1rem;margin-top:1rem}.mini-month h3{font-size:.9rem;margin:.25rem 0}.mini-days{display:grid;grid-template-columns:repeat(7,1fr);gap:3px}.mini-days span{aspect-ratio:1;border-radius:3px;background:#edf1f6}@media(max-width:900px){.top-grid,.year-grid{grid-template-columns:1fr}.page-header,.calendar-toolbar{flex-direction:column}.day{min-height:58px}.qr-box{width:220px}.qr-box img{width:200px;height:200px}}
  `]
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
        this.qrTimer = window.setTimeout(() => this.loadQr(), Math.max(qr.segundosRestantes, 1) * 1000);
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
