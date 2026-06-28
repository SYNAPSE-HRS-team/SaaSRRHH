import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NominaService } from '../../../../core/services/nomina.service';
import { BoletaPagoService } from '../../../../core/services/boleta-pago.service';
import { Planilla, getMesNombre, MESES_NOMBRE } from '../../../../core/models/planilla.model';
import { BoletaPago } from '../../../../core/models/boleta-pago.model';

@Component({
  selector: 'app-nomina',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  template: `
    <div class="page-container">
      <!-- Header -->
      <div class="page-header">
        <div class="header-left">
          <h1>💰 Nómina</h1>
          <p class="subtitle">Generación y gestión de planillas de pago</p>
        </div>
        <div class="header-actions">
          <button class="btn-refresh" (click)="loadPlanillas()" [disabled]="loadingPlanillas()">
            {{ loadingPlanillas() ? '⏳' : '🔄' }} Actualizar
          </button>
          <button class="btn-primary" (click)="showGenerarModal.set(true)" id="btn-generar-planilla">
            ➕ Generar Planilla
          </button>
        </div>
      </div>

      <!-- Stats -->
      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-icon">📋</span>
          <span class="stat-label">Total Planillas</span>
          <span class="stat-value">{{ planillas().length }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">✅</span>
          <span class="stat-label">Procesadas</span>
          <span class="stat-value">{{ countByEstado('PROCESADO') }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🔒</span>
          <span class="stat-label">Cerradas</span>
          <span class="stat-value">{{ countByEstado('CERRADO') }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">💵</span>
          <span class="stat-label">Última Nómina</span>
          <span class="stat-value">{{ getUltimaNomina() }}</span>
        </div>
      </div>

      <!-- Notices -->
      @if (error()) {
        <div class="error-banner">⚠️ {{ error() }}</div>
      }
      @if (success()) {
        <div class="success-banner">✅ {{ success() }}</div>
      }

      <!-- Planillas Table -->
      <div class="card">
        <div class="card-header">
          <h2>Historial de Planillas</h2>
          <div class="filter-group">
            <select class="filter-select" (change)="onFiltroEstado($event)" id="filtro-estado-nomina">
              <option value="">Todos los estados</option>
              <option value="PROCESADO">Procesado</option>
              <option value="CERRADO">Cerrado</option>
            </select>
          </div>
        </div>

        @if (loadingPlanillas()) {
          <div class="loading-state">
            <div class="spinner-dark"></div>
            <p>Cargando planillas...</p>
          </div>
        } @else if (filteredPlanillas().length === 0) {
          <div class="empty-state">
            <div class="empty-icon">📋</div>
            <h3>No hay planillas registradas</h3>
            <p>Genera tu primera planilla de nómina usando el botón de arriba.</p>
          </div>
        } @else {
          <div class="table-container">
            <table>
              <thead>
                <tr>
                  <th>Período</th>
                  <th>Mes / Año</th>
                  <th>Total Pagado</th>
                  <th>Estado</th>
                  <th>Fecha Cierre</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (planilla of filteredPlanillas(); track planilla.id) {
                  <tr>
                    <td>
                      <strong>{{ getMesNombre(planilla.mes) }} {{ planilla.anio }}</strong>
                    </td>
                    <td>{{ planilla.mes }}/{{ planilla.anio }}</td>
                    <td>
                      @if (planilla.totalPagado != null) {
                        <span class="money">S/ {{ planilla.totalPagado | number:'1.2-2' }}</span>
                      } @else {
                        <span class="text-muted">—</span>
                      }
                    </td>
                    <td>
                      <span class="badge" [class]="getEstadoBadge(planilla.estado)">
                        {{ planilla.estado ?? 'PROCESADO' }}
                      </span>
                    </td>
                    <td>
                      @if (planilla.fechaCierre) {
                        {{ planilla.fechaCierre | date:'dd/MM/yyyy' }}
                      } @else {
                        <span class="text-muted">—</span>
                      }
                    </td>
                    <td>
                      <div class="table-actions">
                        <button
                          class="btn-info"
                          (click)="verBoletas(planilla)"
                          [disabled]="loadingBoletas()"
                          id="btn-ver-boletas-{{ planilla.id }}"
                        >
                          🧾 Ver Boletas
                        </button>
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>
    </div>

    <!-- ===== MODAL: GENERAR PLANILLA ===== -->
    @if (showGenerarModal()) {
      <div class="modal-overlay" (click)="closeGenerarModal()">
        <div class="modal-card" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>📋 Generar Nueva Planilla</h3>
            <button class="modal-close" (click)="closeGenerarModal()">✕</button>
          </div>
          <div class="modal-body">
            <p class="modal-desc">
              Selecciona el período para generar la planilla de nómina. El sistema calculará
              automáticamente los sueldos, bonos y descuentos de todos los empleados activos.
            </p>

            @if (generarError()) {
              <div class="error-banner">⚠️ {{ generarError() }}</div>
            }

            <div class="modal-form">
              <div class="form-group">
                <label for="select-mes">Mes</label>
                <select id="select-mes" [(ngModel)]="generarMes" class="form-select">
                  @for (mes of meses; track $index) {
                    <option [value]="$index + 1">{{ mes }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label for="select-anio">Año</label>
                <input
                  type="number"
                  id="select-anio"
                  [(ngModel)]="generarAnio"
                  [min]="2020"
                  [max]="2030"
                  class="form-select"
                />
              </div>
            </div>

            <div class="info-banner" style="margin-top: 1rem; margin-bottom: 0;">
              💡 Si ya existe una planilla para este período, el sistema retornará la existente.
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" (click)="closeGenerarModal()">Cancelar</button>
            <button class="btn-primary" (click)="generarPlanilla()" [disabled]="generando()">
              @if (generando()) {
                <span class="spinner"></span> Generando...
              } @else {
                ⚡ Generar Planilla
              }
            </button>
          </div>
        </div>
      </div>
    }

    <!-- ===== MODAL: VER BOLETAS ===== -->
    @if (showBoletasModal()) {
      <div class="modal-overlay" (click)="showBoletasModal.set(false)">
        <div class="modal-card modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>🧾 Boletas — {{ planillaSeleccionada ? getMesNombre(planillaSeleccionada.mes) + ' ' + planillaSeleccionada.anio : '' }}</h3>
            <button class="modal-close" (click)="showBoletasModal.set(false)">✕</button>
          </div>
          <div class="modal-body">
            @if (loadingBoletas()) {
              <div class="loading-state">
                <div class="spinner-dark"></div>
                <p>Cargando boletas...</p>
              </div>
            } @else if (boletas().length === 0) {
              <div class="empty-state">
                <div class="empty-icon">🧾</div>
                <h3>Sin boletas</h3>
                <p>No se encontraron boletas para esta planilla.</p>
              </div>
            } @else {
              <div class="boletas-summary">
                <span>{{ boletas().length }} boleta(s) encontradas</span>
                <span class="money-positive">Total: S/ {{ getTotalBoletas() | number:'1.2-2' }}</span>
              </div>
              <div class="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Empleado ID</th>
                      <th>Sueldo Base</th>
                      <th>Días Trab.</th>
                      <th>Total Ingr.</th>
                      <th>Total Desc.</th>
                      <th class="highlight-col">Neto Pagar</th>
                      <th>PDF</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (boleta of boletas(); track boleta.id) {
                      <tr>
                        <td>{{ boleta.id }}</td>
                        <td>
                          <span class="badge badge-primary">Emp. #{{ boleta.empleadoId }}</span>
                        </td>
                        <td>S/ {{ boleta.sueldoBase | number:'1.2-2' }}</td>
                        <td>{{ boleta.diasTrabajados }}</td>
                        <td class="money-positive">S/ {{ boleta.totalIngresos | number:'1.2-2' }}</td>
                        <td class="money-negative">S/ {{ boleta.totalDescuentos | number:'1.2-2' }}</td>
                        <td>
                          <strong class="neto-badge">S/ {{ boleta.netoPagar | number:'1.2-2' }}</strong>
                        </td>
                        <td>
                          <button
                            class="btn-pdf"
                            (click)="descargarPdf(boleta.id!)"
                            [disabled]="descargandoPdf() === boleta.id"
                            title="Descargar PDF"
                            id="btn-pdf-{{ boleta.id }}"
                          >
                            @if (descargandoPdf() === boleta.id) {
                              <span class="spinner-sm"></span>
                            } @else {
                              📄
                            }
                          </button>
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" (click)="showBoletasModal.set(false)">Cerrar</button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1.25rem 1.5rem;
      border-bottom: 1px solid var(--color-border);
      flex-wrap: wrap;
      gap: 0.75rem;
    }
    .card-header h2 {
      font-size: 1rem;
      font-weight: 700;
      color: var(--color-primary);
    }

    .text-muted { color: var(--color-text-muted); font-size: 0.85rem; }

    .money { font-weight: 600; color: var(--color-primary); }
    .money-positive { color: var(--color-success); font-weight: 600; }
    .money-negative { color: var(--color-danger); font-weight: 600; }

    .modal-desc {
      font-size: 0.875rem;
      color: var(--color-text-secondary);
      margin-bottom: 1.25rem;
      line-height: 1.6;
    }

    .modal-form {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1rem;
    }

    @media (max-width: 480px) {
      .modal-form { grid-template-columns: 1fr; }
    }

    .boletas-summary {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 0.875rem;
      color: var(--color-text-secondary);
      margin-bottom: 1rem;
      padding: 0.75rem 1rem;
      background: var(--color-bg);
      border-radius: var(--radius-md);
    }

    .highlight-col { background: var(--color-primary-light); }

    .neto-badge {
      display: inline-block;
      padding: 0.2rem 0.6rem;
      background: var(--color-success-light);
      color: var(--color-success);
      border-radius: var(--radius-full);
      font-weight: 700;
      font-size: 0.82rem;
    }

    .btn-pdf {
      width: 32px; height: 32px;
      border: none;
      background: var(--color-primary-light);
      color: var(--color-primary);
      border-radius: 8px;
      cursor: pointer;
      display: flex; align-items: center; justify-content: center;
      font-size: 1rem;
      transition: all 0.2s;
    }
    .btn-pdf:hover:not(:disabled) {
      background: var(--color-accent);
      color: white;
      transform: scale(1.1);
    }
    .btn-pdf:disabled { opacity: 0.5; cursor: not-allowed; }

    .spinner-sm {
      display: inline-block;
      width: 14px; height: 14px;
      border: 2px solid rgba(30,58,95,0.2);
      border-top-color: var(--color-primary);
      border-radius: 50%;
      animation: spin 0.7s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class NominaComponent implements OnInit {
  planillas = signal<Planilla[]>([]);
  filteredPlanillas = signal<Planilla[]>([]);
  boletas = signal<BoletaPago[]>([]);
  loadingPlanillas = signal(false);
  loadingBoletas = signal(false);
  generando = signal(false);
  descargandoPdf = signal<number | null>(null);
  error = signal('');
  success = signal('');
  generarError = signal('');

  showGenerarModal = signal(false);
  showBoletasModal = signal(false);
  planillaSeleccionada: Planilla | null = null;

  generarMes = new Date().getMonth() + 1;
  generarAnio = new Date().getFullYear();
  filtroEstado = '';

  meses = MESES_NOMBRE;
  getMesNombre = getMesNombre;

  constructor(
    private nominaService: NominaService,
    private boletaService: BoletaPagoService
  ) {}

  ngOnInit(): void {
    this.loadPlanillas();
  }

  loadPlanillas(): void {
    this.loadingPlanillas.set(true);
    this.error.set('');
    this.nominaService.listarPlanillas().subscribe({
      next: (data) => {
        // Ordenar por año desc, mes desc
        const sorted = data.sort((a, b) => b.anio !== a.anio ? b.anio - a.anio : b.mes - a.mes);
        this.planillas.set(sorted);
        this.applyFilter();
        this.loadingPlanillas.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar planillas');
        this.loadingPlanillas.set(false);
      }
    });
  }

  applyFilter(): void {
    const all = this.planillas();
    this.filteredPlanillas.set(
      this.filtroEstado ? all.filter(p => p.estado === this.filtroEstado) : all
    );
  }

  onFiltroEstado(event: Event): void {
    this.filtroEstado = (event.target as HTMLSelectElement).value;
    this.applyFilter();
  }

  generarPlanilla(): void {
    this.generarError.set('');
    this.generando.set(true);
    this.nominaService.generarPlanilla(this.generarMes, this.generarAnio).subscribe({
      next: (planilla) => {
        this.generando.set(false);
        this.showGenerarModal.set(false);
        this.success.set(`✅ Planilla de ${getMesNombre(planilla.mes)} ${planilla.anio} generada correctamente.`);
        setTimeout(() => this.success.set(''), 4000);
        this.loadPlanillas();
      },
      error: (err) => {
        this.generando.set(false);
        this.generarError.set(err.error?.message || 'Error al generar la planilla');
      }
    });
  }

  verBoletas(planilla: Planilla): void {
    this.planillaSeleccionada = planilla;
    this.boletas.set([]);
    this.showBoletasModal.set(true);
    this.loadingBoletas.set(true);
    // Cargar todas las boletas y filtrar por planillaId
    this.boletaService.listar().subscribe({
      next: (data) => {
        const filtradas = data.filter(b => b.planillaId === planilla.id);
        this.boletas.set(filtradas);
        this.loadingBoletas.set(false);
      },
      error: () => {
        this.loadingBoletas.set(false);
      }
    });
  }

  descargarPdf(id: number): void {
    this.descargandoPdf.set(id);
    this.boletaService.descargarPdf(id).subscribe({
      next: (blob) => {
        this.boletaService.saveBlob(blob, `boleta-${id}.pdf`);
        this.descargandoPdf.set(null);
      },
      error: () => {
        this.descargandoPdf.set(null);
        this.error.set('No se pudo descargar el PDF de la boleta.');
        setTimeout(() => this.error.set(''), 3000);
      }
    });
  }

  closeGenerarModal(): void {
    if (!this.generando()) {
      this.showGenerarModal.set(false);
      this.generarError.set('');
    }
  }

  countByEstado(estado: string): number {
    return this.planillas().filter(p => (p.estado ?? 'PROCESADO') === estado).length;
  }

  getUltimaNomina(): string {
    const p = this.planillas()[0];
    if (!p) return '—';
    return `${getMesNombre(p.mes)} ${p.anio}`;
  }

  getTotalBoletas(): number {
    return this.boletas().reduce((sum, b) => sum + (b.netoPagar || 0), 0);
  }

  getEstadoBadge(estado?: string): string {
    return estado === 'CERRADO' ? 'badge badge-dark' : 'badge badge-primary';
  }
}
