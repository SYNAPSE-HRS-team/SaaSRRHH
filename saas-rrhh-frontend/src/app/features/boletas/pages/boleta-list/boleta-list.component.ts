import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BoletaPagoService } from '../../../../core/services/boleta-pago.service';
import { BoletaPago } from '../../../../core/models/boleta-pago.model';
import { getMesNombre, MESES_NOMBRE } from '../../../../core/models/planilla.model';

@Component({
  selector: 'app-boleta-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <!-- Header -->
      <div class="page-header">
        <div class="header-left">
          <h1>🧾 Boletas de Pago</h1>
          <p class="subtitle">Consulta y descarga tus boletas de pago</p>
        </div>
        <div class="header-actions">
          <button class="btn-refresh" (click)="load()" [disabled]="loading()">
            {{ loading() ? '⏳' : '🔄' }} Actualizar
          </button>
        </div>
      </div>

      <!-- Stats -->
      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-icon">🧾</span>
          <span class="stat-label">Total Boletas</span>
          <span class="stat-value">{{ boletas().length }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">💵</span>
          <span class="stat-label">Total Neto</span>
          <span class="stat-value" style="font-size: 1.4rem;">S/{{ getTotalNeto() | number:'1.0-0' }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">📈</span>
          <span class="stat-label">Prom. Neto</span>
          <span class="stat-value" style="font-size: 1.4rem;">S/{{ getPromedioNeto() | number:'1.0-0' }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">👥</span>
          <span class="stat-label">Empleados</span>
          <span class="stat-value">{{ getEmpleadosUnicos() }}</span>
        </div>
      </div>

      <!-- Notices -->
      @if (error()) { <div class="error-banner">⚠️ {{ error() }}</div> }

      <!-- Filters -->
      <div class="filters-bar">
        <div class="search-box">
          <i class="search-icon">🔍</i>
          <input
            type="text"
            placeholder="Buscar por ID de empleado o planilla..."
            (input)="onSearch($event)"
          />
        </div>
        <div class="filter-group">
          <select class="filter-select" (change)="onFilterPlanilla($event)" id="filtro-planilla-boleta">
            <option value="">Todas las planillas</option>
            @for (pid of planillasDisponibles(); track pid) {
              <option [value]="pid">Planilla #{{ pid }}</option>
            }
          </select>
        </div>
      </div>

      <!-- Table -->
      <div class="card">
        @if (loading()) {
          <div class="loading-state">
            <div class="spinner-dark"></div>
            <p>Cargando boletas...</p>
          </div>
        } @else if (filtered().length === 0) {
          <div class="empty-state">
            <div class="empty-icon">🧾</div>
            <h3>No hay boletas disponibles</h3>
            <p>Las boletas se generan automáticamente al procesar la nómina.</p>
          </div>
        } @else {
          <div class="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Empleado</th>
                  <th>Planilla</th>
                  <th>Sueldo Base</th>
                  <th>Días Trab.</th>
                  <th>Bonos</th>
                  <th>Descuentos</th>
                  <th>Total Ingr.</th>
                  <th>Total Desc.</th>
                  <th class="col-neto">NETO PAGAR</th>
                  <th>Fecha</th>
                  <th>PDF</th>
                </tr>
              </thead>
              <tbody>
                @for (b of filtered(); track b.id) {
                  <tr [class.row-highlight]="descargandoPdf() === b.id">
                    <td><span class="id-chip">#{{ b.id }}</span></td>
                    <td>
                      <span class="badge badge-primary">Emp. #{{ b.empleadoId }}</span>
                    </td>
                    <td>
                      @if (b.planillaId) {
                        <span class="badge badge-secondary">#{{ b.planillaId }}</span>
                      } @else { — }
                    </td>
                    <td class="num-cell">S/ {{ b.sueldoBase | number:'1.2-2' }}</td>
                    <td class="num-cell center">{{ b.diasTrabajados }}</td>
                    <td class="num-cell money-positive">
                      S/ {{ getTotalBonos(b) | number:'1.2-2' }}
                    </td>
                    <td class="num-cell money-negative">
                      S/ {{ getTotalDescuentos(b) | number:'1.2-2' }}
                    </td>
                    <td class="num-cell money-positive">S/ {{ b.totalIngresos | number:'1.2-2' }}</td>
                    <td class="num-cell money-negative">S/ {{ b.totalDescuentos | number:'1.2-2' }}</td>
                    <td class="col-neto">
                      <div class="neto-pill">S/ {{ b.netoPagar | number:'1.2-2' }}</div>
                    </td>
                    <td class="date-cell">
                      @if (b.fechaEmision) {
                        {{ b.fechaEmision | slice:0:10 }}
                      } @else { — }
                    </td>
                    <td>
                      <button
                        class="pdf-btn"
                        (click)="descargarPdf(b.id!)"
                        [disabled]="descargandoPdf() === b.id"
                        title="Descargar PDF"
                        id="btn-boleta-pdf-{{ b.id }}"
                      >
                        @if (descargandoPdf() === b.id) {
                          <span class="loading-dot"></span>
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

          <div class="table-footer">
            <span>Mostrando {{ filtered().length }} de {{ boletas().length }} boletas</span>
          </div>
        }
      </div>

      <!-- Detalle Modal -->
      @if (boletaDetalle()) {
        <div class="modal-overlay" (click)="boletaDetalle.set(null)">
          <div class="modal-card" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3>🧾 Detalle Boleta #{{ boletaDetalle()!.id }}</h3>
              <button class="modal-close" (click)="boletaDetalle.set(null)">✕</button>
            </div>
            <div class="modal-body">
              <div class="boleta-detail">
                <div class="detail-section">
                  <h4>Ingresos</h4>
                  <div class="detail-row">
                    <span>Sueldo Base</span>
                    <span class="money-positive">S/ {{ boletaDetalle()!.sueldoBase | number:'1.2-2' }}</span>
                  </div>
                  <div class="detail-row">
                    <span>Asignación Familiar</span>
                    <span class="money-positive">S/ {{ boletaDetalle()!.asignacionFamiliar | number:'1.2-2' }}</span>
                  </div>
                  <div class="detail-row">
                    <span>Bono Beta</span>
                    <span class="money-positive">S/ {{ boletaDetalle()!.bonoBeta | number:'1.2-2' }}</span>
                  </div>
                  <div class="detail-row">
                    <span>Horas Extra</span>
                    <span class="money-positive">S/ {{ boletaDetalle()!.horasExtraPago | number:'1.2-2' }}</span>
                  </div>
                  <div class="detail-row">
                    <span>Otros Bonos</span>
                    <span class="money-positive">S/ {{ boletaDetalle()!.otrosBonos | number:'1.2-2' }}</span>
                  </div>
                  <div class="detail-row total-row">
                    <span>Total Ingresos</span>
                    <span class="money-positive">S/ {{ boletaDetalle()!.totalIngresos | number:'1.2-2' }}</span>
                  </div>
                </div>
                <div class="detail-section">
                  <h4>Descuentos</h4>
                  <div class="detail-row">
                    <span>Desc. Inasistencia</span>
                    <span class="money-negative">- S/ {{ boletaDetalle()!.descuentoInasistencia | number:'1.2-2' }}</span>
                  </div>
                  <div class="detail-row">
                    <span>Otros Descuentos</span>
                    <span class="money-negative">- S/ {{ boletaDetalle()!.otrosDescuentos | number:'1.2-2' }}</span>
                  </div>
                  <div class="detail-row total-row">
                    <span>Total Descuentos</span>
                    <span class="money-negative">- S/ {{ boletaDetalle()!.totalDescuentos | number:'1.2-2' }}</span>
                  </div>
                </div>
                <div class="neto-section">
                  <span>Neto a Pagar</span>
                  <span>S/ {{ boletaDetalle()!.netoPagar | number:'1.2-2' }}</span>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn-secondary" (click)="boletaDetalle.set(null)">Cerrar</button>
              <button class="btn-primary" (click)="descargarPdf(boletaDetalle()!.id!)">
                📄 Descargar PDF
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .id-chip { background: var(--color-bg); color: var(--color-text-muted); font-size: 0.75rem; font-weight: 600; padding: 0.15rem 0.45rem; border-radius: 4px; }
    .num-cell { font-family: 'Inter', monospace; font-size: 0.82rem; }
    .money-positive { color: var(--color-success); font-weight: 600; }
    .money-negative { color: var(--color-danger); font-weight: 600; }
    .center { text-align: center; }
    .date-cell { font-size: 0.8rem; color: var(--color-text-muted); white-space: nowrap; }

    .col-neto {
      background: var(--color-primary-light);
      font-weight: 800;
    }

    .neto-pill {
      background: var(--color-primary);
      color: white;
      border-radius: var(--radius-full);
      padding: 0.25rem 0.75rem;
      font-weight: 700;
      font-size: 0.82rem;
      white-space: nowrap;
      display: inline-block;
    }

    .row-highlight { background: var(--color-accent-light) !important; }

    .pdf-btn {
      width: 34px; height: 34px;
      border: none;
      background: var(--color-primary-light);
      color: var(--color-primary);
      border-radius: 8px;
      cursor: pointer;
      display: flex; align-items: center; justify-content: center;
      font-size: 1rem;
      transition: all 0.2s;
    }
    .pdf-btn:hover:not(:disabled) { background: var(--color-accent); color: white; transform: scale(1.1); box-shadow: 0 4px 10px rgba(37,99,235,0.3); }
    .pdf-btn:disabled { opacity: 0.5; cursor: not-allowed; }

    .loading-dot {
      display: inline-block;
      width: 14px; height: 14px;
      border: 2px solid var(--color-border);
      border-top-color: var(--color-accent);
      border-radius: 50%;
      animation: spin 0.7s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }

    .table-footer {
      padding: 0.875rem 1rem;
      border-top: 1px solid var(--color-border);
      font-size: 0.8rem;
      color: var(--color-text-muted);
      background: var(--color-bg);
    }

    /* Boleta Detail */
    .boleta-detail { display: flex; flex-direction: column; gap: 1.25rem; }
    .detail-section { background: var(--color-bg); border-radius: var(--radius-md); padding: 1rem; }
    .detail-section h4 { font-size: 0.8rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; color: var(--color-text-muted); margin-bottom: 0.75rem; }
    .detail-row { display: flex; justify-content: space-between; align-items: center; padding: 0.35rem 0; font-size: 0.875rem; }
    .detail-row.total-row { border-top: 1px dashed var(--color-border); margin-top: 0.5rem; padding-top: 0.5rem; font-weight: 700; }
    .neto-section {
      display: flex; justify-content: space-between; align-items: center;
      background: var(--color-primary);
      color: white;
      border-radius: var(--radius-md);
      padding: 1rem 1.25rem;
      font-size: 1.1rem;
      font-weight: 800;
    }
  `]
})
export class BoletaListComponent implements OnInit {
  boletas = signal<BoletaPago[]>([]);
  filtered = signal<BoletaPago[]>([]);
  loading = signal(false);
  descargandoPdf = signal<number | null>(null);
  error = signal('');
  boletaDetalle = signal<BoletaPago | null>(null);

  searchTerm = '';
  filterPlanilla = '';
  getMesNombre = getMesNombre;

  constructor(private boletaService: BoletaPagoService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.boletaService.listar().subscribe({
      next: data => {
        this.boletas.set(data);
        this.applyFilter();
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'Error al cargar boletas');
        this.loading.set(false);
      }
    });
  }

  applyFilter(): void {
    let result = this.boletas();
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(b =>
        b.empleadoId?.toString().includes(term) ||
        b.planillaId?.toString().includes(term) ||
        b.id?.toString().includes(term)
      );
    }
    if (this.filterPlanilla) {
      result = result.filter(b => b.planillaId === +this.filterPlanilla);
    }
    this.filtered.set(result);
  }

  onSearch(e: Event): void { this.searchTerm = (e.target as HTMLInputElement).value; this.applyFilter(); }
  onFilterPlanilla(e: Event): void { this.filterPlanilla = (e.target as HTMLSelectElement).value; this.applyFilter(); }

  planillasDisponibles(): number[] {
    return [...new Set(this.boletas().map(b => b.planillaId).filter(Boolean) as number[])].sort((a, b) => b - a);
  }

  getTotalBonos(b: BoletaPago): number {
    return (b.asignacionFamiliar || 0) + (b.bonoBeta || 0) + (b.horasExtraPago || 0) + (b.otrosBonos || 0);
  }

  getTotalDescuentos(b: BoletaPago): number {
    return (b.descuentoInasistencia || 0) + (b.otrosDescuentos || 0);
  }

  getTotalNeto(): number {
    return this.boletas().reduce((s, b) => s + (b.netoPagar || 0), 0);
  }

  getPromedioNeto(): number {
    const total = this.boletas().length;
    return total > 0 ? this.getTotalNeto() / total : 0;
  }

  getEmpleadosUnicos(): number {
    return new Set(this.boletas().map(b => b.empleadoId)).size;
  }

  descargarPdf(id: number): void {
    this.descargandoPdf.set(id);
    this.boletaService.descargarPdf(id).subscribe({
      next: blob => {
        this.boletaService.saveBlob(blob, `boleta-${id}.pdf`);
        this.descargandoPdf.set(null);
      },
      error: () => {
        this.descargandoPdf.set(null);
        this.error.set('No se pudo descargar el PDF.');
        setTimeout(() => this.error.set(''), 3000);
      }
    });
  }
}
