import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';

@Component({
  selector: 'app-empleado-list',
  standalone: true,
  imports: [RouterLink, DatePipe],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div class="header-left">
          <h1>Empleados</h1>
          <p class="subtitle">Gestión de empleados de la empresa</p>
        </div>
        <div class="header-actions">
          <button class="btn-refresh" (click)="loadEmpleados()" [disabled]="loading()">
            <span>🔄</span> Actualizar
          </button>
          <a routerLink="/empleados/nuevo" class="btn-primary">
            <span>➕</span> Nuevo Empleado
          </a>
        </div>
      </div>

      <!-- Filtros -->
      <div class="filters-bar">
        <div class="search-box">
          <span class="search-icon">🔍</span>
          <input
            type="text"
            placeholder="Buscar por nombre, apellido, DNI o cargo..."
            (input)="onSearch($event)"
          />
        </div>
        <div class="filter-group">
          <select (change)="onFilterEstado($event)" class="filter-select">
            <option value="">Todos los estados</option>
            <option value="true">Activos</option>
            <option value="false">Inactivos</option>
          </select>
          <select (change)="onFilterCargo($event)" class="filter-select">
            <option value="">Todos los cargos</option>
            @for (cargo of cargosDisponibles; track cargo) {
              <option [value]="cargo">{{ cargo }}</option>
            }
          </select>
        </div>
      </div>

      <!-- Loading -->
      @if (loading()) {
        <div class="loading-state">
          <div class="spinner-lg"></div>
          <p>Cargando empleados...</p>
        </div>
      }

      <!-- Error -->
      @if (error()) {
        <div class="error-state">
          <span class="error-icon">⚠️</span>
          <p>{{ error() }}</p>
          <button class="btn-retry" (click)="loadEmpleados()">Reintentar</button>
        </div>
      }

      <!-- Empty -->
      @if (!loading() && !error() && filteredEmpleados().length === 0) {
        <div class="empty-state">
          <span class="empty-icon">👥</span>
          <h3>No hay empleados registrados</h3>
          <p>Comienza registrando un nuevo empleado en el sistema.</p>
          <a routerLink="/empleados/nuevo" class="btn-primary">Registrar Empleado</a>
        </div>
      }

      <!-- Tabla -->
      @if (!loading() && !error() && filteredEmpleados().length > 0) {
        <div class="table-container">
          <table class="empleados-table">
            <thead>
              <tr>
                <th>DNI</th>
                <th>Nombres</th>
                <th>Apellidos</th>
                <th>Email</th>
                <th>Cargo</th>
                <th>Sueldo Base</th>
                <th>Inicio Contrato</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              @for (emp of filteredEmpleados(); track emp.id) {
                <tr>
                  <td><span class="dni-badge">{{ emp.dni }}</span></td>
                  <td class="name-cell">{{ emp.nombres }}</td>
                  <td>{{ emp.apellidos }}</td>
                  <td class="email-cell">{{ emp.email || '—' }}</td>
                  <td><span class="cargo-badge">{{ emp.cargo || '—' }}</span></td>
                  <td class="moneda">S/ {{ emp.sueldoBase?.toFixed(2) || '0.00' }}</td>
                  <td>{{ emp.fechaInicioContrato | date:'dd/MM/yyyy' }}</td>
                  <td>
                    @if (emp.activo) {
                      <span class="status-active">Activo</span>
                    } @else {
                      <span class="status-inactive">Inactivo</span>
                    }
                  </td>
                  <td class="actions-cell">
                    <button class="btn-icon" title="Ver detalle" (click)="verDetalle(emp)">
                      👁️
                    </button>
                    <button class="btn-icon" title="Eliminar" (click)="confirmarEliminar(emp)">
                      🗑️
                    </button>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Contador -->
        <div class="table-footer">
          <span>Mostrando {{ filteredEmpleados().length }} de {{ empleados().length }} empleados</span>
        </div>
      }
    </div>

    <!-- Modal de confirmación -->
    @if (showDeleteModal()) {
      <div class="modal-overlay" (click)="showDeleteModal.set(false)">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <span class="modal-icon">⚠️</span>
            <h3>Confirmar Eliminación</h3>
          </div>
          <p>¿Estás seguro de eliminar a <strong>{{ empleadoToDelete?.nombres }} {{ empleadoToDelete?.apellidos }}</strong>?</p>
          <p class="modal-warning">Esta acción no se puede deshacer.</p>
          <div class="modal-actions">
            <button class="btn-cancel" (click)="showDeleteModal.set(false)">Cancelar</button>
            <button class="btn-danger" (click)="eliminarEmpleado()" [disabled]="deleting()">
              @if (deleting()) {
                <span class="spinner-sm"></span> Eliminando...
              } @else {
                Eliminar
              }
            </button>
          </div>
        </div>
      </div>
    }

    <!-- Modal de detalle -->
    @if (showDetailModal() && empleadoDetalle) {
      <div class="modal-overlay" (click)="showDetailModal.set(false)">
        <div class="modal-content modal-detail" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <span class="modal-icon">👤</span>
            <h3>Detalle del Empleado</h3>
            <button class="btn-close" (click)="showDetailModal.set(false)">✕</button>
          </div>
          <div class="detail-grid">
            <div class="detail-field">
              <label>DNI</label>
              <span>{{ empleadoDetalle.dni }}</span>
            </div>
            <div class="detail-field">
              <label>Nombres</label>
              <span>{{ empleadoDetalle.nombres }}</span>
            </div>
            <div class="detail-field">
              <label>Apellidos</label>
              <span>{{ empleadoDetalle.apellidos }}</span>
            </div>
            <div class="detail-field">
              <label>Email</label>
              <span>{{ empleadoDetalle.email || '—' }}</span>
            </div>
            <div class="detail-field">
              <label>Cargo</label>
              <span>{{ empleadoDetalle.cargo || '—' }}</span>
            </div>
            <div class="detail-field">
              <label>Sueldo Base</label>
              <span>S/ {{ empleadoDetalle.sueldoBase?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="detail-field">
              <label>Inicio Contrato</label>
              <span>{{ empleadoDetalle.fechaInicioContrato | date:'dd/MM/yyyy' }}</span>
            </div>
            <div class="detail-field">
              <label>Fin Contrato</label>
              <span>{{ empleadoDetalle.fechaFinContrato ? (empleadoDetalle.fechaFinContrato | date:'dd/MM/yyyy') : 'Indefinido' }}</span>
            </div>
            <div class="detail-field">
              <label>Asignación Familiar</label>
              <span>{{ empleadoDetalle.asignacionFamiliar ? 'Sí' : 'No' }}</span>
            </div>
            <div class="detail-field">
              <label>Estado</label>
              <span>
                @if (empleadoDetalle.activo) {
                  <span class="status-active">Activo</span>
                } @else {
                  <span class="status-inactive">Inactivo</span>
                }
              </span>
            </div>
            <div class="detail-field full-width">
              <label>Fecha de Registro</label>
              <span>{{ empleadoDetalle.fechaRegistro | date:'dd/MM/yyyy HH:mm' }}</span>
            </div>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .page-container { max-width: 1400px; margin: 0 auto; }
    .page-header {
      display: flex; justify-content: space-between; align-items: flex-start;
      margin-bottom: 1.5rem;
    }
    .header-left h1 { margin: 0; color: #1a1a2e; font-size: 1.75rem; }
    .subtitle { color: #666; margin: 0.25rem 0 0; }
    .header-actions { display: flex; gap: 0.75rem; }

    .btn-primary {
      display: inline-flex; align-items: center; gap: 0.5rem;
      padding: 0.7rem 1.25rem;
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white; border: none; border-radius: 8px;
      font-size: 0.9rem; font-weight: 600; cursor: pointer;
      text-decoration: none; transition: all 0.2s;
    }
    .btn-primary:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(102,126,234,0.4); }

    .btn-refresh {
      display: inline-flex; align-items: center; gap: 0.5rem;
      padding: 0.7rem 1.25rem;
      background: white; color: #555; border: 2px solid #e0e0e0; border-radius: 8px;
      font-size: 0.9rem; font-weight: 500; cursor: pointer;
      transition: all 0.2s;
    }
    .btn-refresh:hover:not(:disabled) { border-color: #667eea; color: #667eea; }
    .btn-refresh:disabled { opacity: 0.6; cursor: not-allowed; }

    .filters-bar {
      display: flex; gap: 1rem; align-items: center;
      background: white; padding: 1rem; border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04); margin-bottom: 1.25rem;
    }
    .search-box {
      flex: 1; display: flex; align-items: center; gap: 0.5rem;
      background: #f5f6fa; padding: 0.5rem 1rem; border-radius: 8px;
    }
    .search-box input {
      border: none; background: transparent; outline: none;
      flex: 1; font-size: 0.9rem;
    }
    .filter-group { display: flex; gap: 0.75rem; }
    .filter-select {
      padding: 0.5rem 0.75rem; border: 2px solid #e0e0e0; border-radius: 8px;
      font-size: 0.85rem; background: white; cursor: pointer; outline: none;
    }
    .filter-select:focus { border-color: #667eea; }

    .loading-state, .error-state, .empty-state {
      display: flex; flex-direction: column; align-items: center;
      justify-content: center; padding: 4rem 2rem;
      background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
    .spinner-lg {
      width: 40px; height: 40px;
      border: 3px solid #e0e0e0; border-top-color: #667eea;
      border-radius: 50%; animation: spin 0.6s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
    .error-icon, .empty-icon { font-size: 3rem; margin-bottom: 1rem; }
    .error-state p { color: #e74c3c; }
    .btn-retry {
      margin-top: 1rem; padding: 0.6rem 1.5rem;
      background: #667eea; color: white; border: none; border-radius: 8px;
      cursor: pointer; font-weight: 500;
    }

    .table-container {
      background: white; border-radius: 12px; overflow: hidden;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
    .empleados-table { width: 100%; border-collapse: collapse; }
    .empleados-table th {
      padding: 0.85rem 1rem; text-align: left;
      font-size: 0.8rem; font-weight: 600; color: #888;
      text-transform: uppercase; letter-spacing: 0.5px;
      background: #fafafa; border-bottom: 2px solid #f0f0f0;
    }
    .empleados-table td {
      padding: 0.75rem 1rem; font-size: 0.9rem; color: #333;
      border-bottom: 1px solid #f0f0f0;
    }
    .empleados-table tr:hover { background: #f8f9ff; }
    .empleados-table tr:last-child td { border-bottom: none; }

    .dni-badge {
      font-family: monospace; background: #f0f0f0;
      padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.85rem;
    }
    .cargo-badge {
      background: #eef2ff; color: #667eea;
      padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500;
    }
    .name-cell { font-weight: 600; color: #1a1a2e; }
    .email-cell { color: #666; font-size: 0.85rem; }
    .moneda { font-family: monospace; font-weight: 600; color: #1a1a2e; }

    .status-active {
      background: #ecfdf5; color: #059669;
      padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500;
    }
    .status-inactive {
      background: #fef2f2; color: #dc2626;
      padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500;
    }

    .actions-cell { display: flex; gap: 0.5rem; }
    .btn-icon {
      width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
      border: none; background: transparent; border-radius: 6px; cursor: pointer;
      font-size: 1rem; transition: background 0.2s;
    }
    .btn-icon:hover { background: #f0f0f0; }

    .table-footer {
      padding: 0.75rem 1rem; font-size: 0.85rem; color: #888;
      border-top: 1px solid #f0f0f0; background: #fafafa;
    }

    /* Modal */
    .modal-overlay {
      position: fixed; inset: 0; background: rgba(0,0,0,0.5);
      display: flex; align-items: center; justify-content: center;
      z-index: 2000; animation: fadeIn 0.2s;
    }
    @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    .modal-content {
      background: white; border-radius: 16px; padding: 2rem;
      max-width: 480px; width: 90%; box-shadow: 0 20px 60px rgba(0,0,0,0.15);
    }
    .modal-detail { max-width: 600px; }
    .modal-header { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; color: #1a1a2e; }
    .modal-icon { font-size: 1.5rem; }
    .btn-close {
      margin-left: auto; width: 28px; height: 28px;
      border: none; background: transparent; border-radius: 6px;
      cursor: pointer; font-size: 1rem; color: #888;
    }
    .btn-close:hover { background: #f0f0f0; }

    .modal-warning { color: #e74c3c; font-size: 0.85rem; }
    .modal-actions { display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem; }

    .btn-cancel {
      padding: 0.6rem 1.25rem; border: 2px solid #e0e0e0; border-radius: 8px;
      background: white; cursor: pointer; font-weight: 500; color: #555;
    }
    .btn-danger {
      padding: 0.6rem 1.25rem; background: #dc2626; color: white;
      border: none; border-radius: 8px; cursor: pointer; font-weight: 600;
      display: flex; align-items: center; gap: 0.5rem;
    }
    .btn-danger:hover:not(:disabled) { background: #b91c1c; }
    .btn-danger:disabled { opacity: 0.6; cursor: not-allowed; }

    .spinner-sm {
      width: 14px; height: 14px;
      border: 2px solid rgba(255,255,255,0.3); border-top-color: white;
      border-radius: 50%; animation: spin 0.6s linear infinite;
    }

    .detail-grid {
      display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;
    }
    .detail-field.full-width { grid-column: 1 / -1; }
    .detail-field label {
      display: block; font-size: 0.75rem; font-weight: 600; color: #888;
      text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 0.25rem;
    }
    .detail-field span { font-size: 0.95rem; color: #1a1a2e; font-weight: 500; }
  `]
})
export class EmpleadoListComponent implements OnInit {
  empleados = signal<EmpleadoResponse[]>([]);
  filteredEmpleados = signal<EmpleadoResponse[]>([]);
  cargosDisponibles: string[] = [];
  loading = signal(false);
  error = signal('');
  searchTerm = '';
  filterEstado = '';
  filterCargo = '';

  // Delete modal
  showDeleteModal = signal(false);
  empleadoToDelete: EmpleadoResponse | null = null;
  deleting = signal(false);

  // Detail modal
  showDetailModal = signal(false);
  empleadoDetalle: EmpleadoResponse | null = null;

  constructor(private empleadoService: EmpleadoService) {}

  ngOnInit(): void {
    this.loadEmpleados();
  }

  loadEmpleados(): void {
    this.loading.set(true);
    this.error.set('');

    this.empleadoService.getAll().subscribe({
      next: (data) => {
        this.empleados.set(data);
        this.applyFilters();
        this.cargosDisponibles = [...new Set(data.map(e => e.cargo).filter(Boolean) as string[])];
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Error al cargar empleados: ' + (err.error?.message || err.message));
        this.loading.set(false);
      }
    });
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    this.applyFilters();
  }

  onFilterEstado(event: Event): void {
    this.filterEstado = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  onFilterCargo(event: Event): void {
    this.filterCargo = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  private applyFilters(): void {
    let result = this.empleados();

    if (this.searchTerm) {
      result = result.filter(e =>
        e.nombres.toLowerCase().includes(this.searchTerm) ||
        e.apellidos.toLowerCase().includes(this.searchTerm) ||
        e.dni.toLowerCase().includes(this.searchTerm) ||
        (e.cargo || '').toLowerCase().includes(this.searchTerm)
      );
    }

    if (this.filterEstado) {
      result = result.filter(e => e.activo === (this.filterEstado === 'true'));
    }

    if (this.filterCargo) {
      result = result.filter(e => e.cargo === this.filterCargo);
    }

    this.filteredEmpleados.set(result);
  }

  verDetalle(emp: EmpleadoResponse): void {
    this.empleadoDetalle = emp;
    this.showDetailModal.set(true);
  }

  confirmarEliminar(emp: EmpleadoResponse): void {
    this.empleadoToDelete = emp;
    this.showDeleteModal.set(true);
  }

  eliminarEmpleado(): void {
    if (!this.empleadoToDelete) return;
    this.deleting.set(true);

    this.empleadoService.delete(this.empleadoToDelete.id).subscribe({
      next: () => {
        this.showDeleteModal.set(false);
        this.deleting.set(false);
        this.empleadoToDelete = null;
        this.loadEmpleados();
      },
      error: (err) => {
        this.deleting.set(false);
        alert('Error al eliminar empleado: ' + (err.error?.message || err.message));
      }
    });
  }
}
