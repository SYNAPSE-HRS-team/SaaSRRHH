import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanillaService } from '../../../../core/services/planilla.service';
import { Planilla, PlanillaRequest, EstadoPlanilla, getMesNombre, MESES_NOMBRE } from '../../../../core/models/planilla.model';

@Component({
  selector: 'app-planilla-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div class="header-left">
          <h1>📋 Planillas</h1>
          <p class="subtitle">Administración completa de planillas de pago</p>
        </div>
        <div class="header-actions">
          <button class="btn-refresh" (click)="load()" [disabled]="loading()">
            {{ loading() ? '⏳' : '🔄' }} Actualizar
          </button>
          <button class="btn-primary" (click)="openForm(null)" id="btn-nueva-planilla">
            ➕ Nueva Planilla
          </button>
        </div>
      </div>

      <!-- Stats -->
      <div class="stats-grid" style="grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));">
        <div class="stat-card">
          <span class="stat-icon">📋</span>
          <span class="stat-label">Total</span>
          <span class="stat-value">{{ planillas().length }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🟢</span>
          <span class="stat-label">Procesadas</span>
          <span class="stat-value">{{ countEstado('PROCESADO') }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🔒</span>
          <span class="stat-label">Cerradas</span>
          <span class="stat-value">{{ countEstado('CERRADO') }}</span>
        </div>
      </div>

      @if (error()) {
        <div class="error-banner">⚠️ {{ error() }}</div>
      }
      @if (success()) {
        <div class="success-banner">✅ {{ success() }}</div>
      }

      <!-- Filters -->
      <div class="filters-bar">
        <div class="filter-group">
          <select class="filter-select" (change)="onFilterEstado($event)" id="filtro-planilla-estado">
            <option value="">Todos los estados</option>
            <option value="PROCESADO">Procesado</option>
            <option value="CERRADO">Cerrado</option>
          </select>
          <select class="filter-select" (change)="onFilterAnio($event)" id="filtro-planilla-anio">
            <option value="">Todos los años</option>
            @for (anio of aniosDisponibles(); track anio) {
              <option [value]="anio">{{ anio }}</option>
            }
          </select>
        </div>
      </div>

      <!-- Table -->
      <div class="card">
        @if (loading()) {
          <div class="loading-state">
            <div class="spinner-dark"></div>
            <p>Cargando planillas...</p>
          </div>
        } @else if (filtered().length === 0) {
          <div class="empty-state">
            <div class="empty-icon">📋</div>
            <h3>No hay planillas</h3>
            <p>Crea una nueva planilla o revisa los filtros aplicados.</p>
            <button class="btn-primary" (click)="openForm(null)">➕ Nueva Planilla</button>
          </div>
        } @else {
          <div class="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Período</th>
                  <th>Mes</th>
                  <th>Año</th>
                  <th>Total Pagado</th>
                  <th>Estado</th>
                  <th>Fecha Cierre</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (p of filtered(); track p.id) {
                  <tr>
                    <td><span class="id-chip">#{{ p.id }}</span></td>
                    <td><strong>{{ getMesNombre(p.mes) }} {{ p.anio }}</strong></td>
                    <td>{{ p.mes }}</td>
                    <td>{{ p.anio }}</td>
                    <td>
                      @if (p.totalPagado != null) {
                        <span class="money-value">S/ {{ p.totalPagado | number:'1.2-2' }}</span>
                      } @else { — }
                    </td>
                    <td>
                      <span class="badge" [class]="p.estado === 'CERRADO' ? 'badge-dark' : 'badge-primary'">
                        {{ p.estado ?? 'PROCESADO' }}
                      </span>
                    </td>
                    <td>
                      @if (p.fechaCierre) {
                        {{ p.fechaCierre | slice:0:10 }}
                      } @else { — }
                    </td>
                    <td>
                      <div class="table-actions">
                        <button class="btn-info" (click)="openForm(p)" title="Editar" id="btn-editar-planilla-{{ p.id }}">✏️</button>
                        <button class="btn-danger" (click)="confirmarEliminar(p)" title="Eliminar" id="btn-eliminar-planilla-{{ p.id }}">🗑️</button>
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

    <!-- ===== MODAL FORM ===== -->
    @if (showForm()) {
      <div class="modal-overlay" (click)="closeForm()">
        <div class="modal-card" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ editando ? '✏️ Editar Planilla' : '➕ Nueva Planilla' }}</h3>
            <button class="modal-close" (click)="closeForm()">✕</button>
          </div>
          <div class="modal-body">
            @if (formError()) {
              <div class="error-banner">⚠️ {{ formError() }}</div>
            }
            <div class="form-grid" style="gap: 1rem;">
              <div class="form-group">
                <label for="form-mes">Mes <span class="required">*</span></label>
                <select id="form-mes" [(ngModel)]="form.mes" class="form-select">
                  @for (mes of meses; track $index) {
                    <option [value]="$index + 1">{{ mes }}</option>
                  }
                </select>
              </div>
              <div class="form-group">
                <label for="form-anio">Año <span class="required">*</span></label>
                <input type="number" id="form-anio" [(ngModel)]="form.anio" [min]="2020" [max]="2030" class="form-select" />
              </div>
              <div class="form-group">
                <label for="form-total">Total Pagado (S/)</label>
                <input type="number" id="form-total" [(ngModel)]="form.totalPagado" step="0.01" min="0" class="form-select" placeholder="0.00" />
              </div>
              <div class="form-group">
                <label for="form-estado">Estado</label>
                <select id="form-estado" [(ngModel)]="form.estado" class="form-select">
                  <option value="PROCESADO">PROCESADO</option>
                  <option value="CERRADO">CERRADO</option>
                </select>
              </div>
              <div class="form-group" style="grid-column: 1/-1;">
                <label for="form-cierre">Fecha de Cierre</label>
                <input type="date" id="form-cierre" [(ngModel)]="form.fechaCierre" class="form-select" />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" (click)="closeForm()">Cancelar</button>
            <button class="btn-primary" (click)="guardar()" [disabled]="saving()">
              @if (saving()) { <span class="spinner"></span> Guardando... }
              @else { 💾 Guardar }
            </button>
          </div>
        </div>
      </div>
    }

    <!-- ===== MODAL CONFIRMAR ELIMINAR ===== -->
    @if (showDelete()) {
      <div class="modal-overlay" (click)="showDelete.set(false)">
        <div class="modal-card modal-sm" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>⚠️ Confirmar Eliminación</h3>
            <button class="modal-close" (click)="showDelete.set(false)">✕</button>
          </div>
          <div class="modal-body">
            <p style="color: var(--color-text-secondary); font-size: 0.9rem;">
              ¿Estás seguro de eliminar la planilla de
              <strong>{{ planillaToDelete ? getMesNombre(planillaToDelete.mes) + ' ' + planillaToDelete.anio : '' }}</strong>?
              Esta acción no se puede deshacer.
            </p>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" (click)="showDelete.set(false)">Cancelar</button>
            <button class="btn-danger" style="padding: 0.6rem 1.25rem;" (click)="eliminar()" [disabled]="deleting()">
              @if (deleting()) { <span class="spinner" style="border-top-color: var(--color-danger);"></span> Eliminando... }
              @else { 🗑️ Eliminar }
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .id-chip { background: var(--color-bg); color: var(--color-text-muted); font-size: 0.78rem; font-weight: 600; padding: 0.15rem 0.5rem; border-radius: 4px; }
    .money-value { font-weight: 700; color: var(--color-primary); }
    .required { color: var(--color-danger); }
  `]
})
export class PlanillaListComponent implements OnInit {
  planillas = signal<Planilla[]>([]);
  filtered = signal<Planilla[]>([]);
  loading = signal(false);
  saving = signal(false);
  deleting = signal(false);
  error = signal('');
  success = signal('');
  formError = signal('');
  showForm = signal(false);
  showDelete = signal(false);

  editando = false;
  editId: number | null = null;
  planillaToDelete: Planilla | null = null;

  filterEstado = '';
  filterAnio = '';

  form: Partial<PlanillaRequest> & { estado?: EstadoPlanilla; fechaCierre?: string } = this.emptyForm();
  meses = MESES_NOMBRE;
  getMesNombre = getMesNombre;

  constructor(private planillaService: PlanillaService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.planillaService.listar().subscribe({
      next: data => {
        const sorted = data.sort((a, b) => b.anio !== a.anio ? b.anio - a.anio : b.mes - a.mes);
        this.planillas.set(sorted);
        this.applyFilter();
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'Error al cargar planillas');
        this.loading.set(false);
      }
    });
  }

  applyFilter(): void {
    let result = this.planillas();
    if (this.filterEstado) result = result.filter(p => (p.estado ?? 'PROCESADO') === this.filterEstado);
    if (this.filterAnio) result = result.filter(p => p.anio === +this.filterAnio);
    this.filtered.set(result);
  }

  onFilterEstado(e: Event): void { this.filterEstado = (e.target as HTMLSelectElement).value; this.applyFilter(); }
  onFilterAnio(e: Event): void { this.filterAnio = (e.target as HTMLSelectElement).value; this.applyFilter(); }

  aniosDisponibles(): number[] {
    return [...new Set(this.planillas().map(p => p.anio))].sort((a, b) => b - a);
  }

  countEstado(estado: string): number {
    return this.planillas().filter(p => (p.estado ?? 'PROCESADO') === estado).length;
  }

  openForm(planilla: Planilla | null): void {
    this.formError.set('');
    if (planilla) {
      this.editando = true;
      this.editId = planilla.id ?? null;
      this.form = {
        mes: planilla.mes,
        anio: planilla.anio,
        totalPagado: planilla.totalPagado,
        estado: planilla.estado ?? 'PROCESADO',
        fechaCierre: planilla.fechaCierre?.slice(0, 10)
      };
    } else {
      this.editando = false;
      this.editId = null;
      this.form = this.emptyForm();
    }
    this.showForm.set(true);
  }

  closeForm(): void {
    if (!this.saving()) this.showForm.set(false);
  }

  guardar(): void {
    if (!this.form.mes || !this.form.anio) {
      this.formError.set('Mes y año son obligatorios');
      return;
    }
    this.saving.set(true);
    this.formError.set('');
    const req$ = this.editando && this.editId
      ? this.planillaService.actualizar(this.editId, this.form as PlanillaRequest)
      : this.planillaService.guardar(this.form as PlanillaRequest);

    req$.subscribe({
      next: () => {
        this.saving.set(false);
        this.showForm.set(false);
        this.success.set(this.editando ? 'Planilla actualizada correctamente.' : 'Planilla creada correctamente.');
        setTimeout(() => this.success.set(''), 4000);
        this.load();
      },
      error: err => {
        this.saving.set(false);
        this.formError.set(err.error?.message || 'Error al guardar la planilla');
      }
    });
  }

  confirmarEliminar(p: Planilla): void {
    this.planillaToDelete = p;
    this.showDelete.set(true);
  }

  eliminar(): void {
    if (!this.planillaToDelete?.id) return;
    this.deleting.set(true);
    this.planillaService.eliminar(this.planillaToDelete.id).subscribe({
      next: () => {
        this.deleting.set(false);
        this.showDelete.set(false);
        this.planillaToDelete = null;
        this.success.set('Planilla eliminada.');
        setTimeout(() => this.success.set(''), 3000);
        this.load();
      },
      error: err => {
        this.deleting.set(false);
        this.error.set(err.error?.message || 'Error al eliminar la planilla');
        this.showDelete.set(false);
      }
    });
  }

  private emptyForm(): Partial<PlanillaRequest> & { estado: EstadoPlanilla } {
    return {
      mes: new Date().getMonth() + 1,
      anio: new Date().getFullYear(),
      totalPagado: undefined,
      estado: 'PROCESADO',
      fechaCierre: undefined
    };
  }
}
