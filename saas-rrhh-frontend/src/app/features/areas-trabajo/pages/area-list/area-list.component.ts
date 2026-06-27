import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AreaTrabajoService } from '../../../../core/services/area-trabajo.service';
import { AreaTrabajo } from '../../../../core/models/area-trabajo.model';

@Component({
  selector: 'app-area-list',
  standalone: true,
  imports: [RouterLink, DatePipe],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div class="header-left">
          <h1>Áreas de Trabajo</h1>
          <p class="subtitle">Gestión de áreas y cultivos de la empresa</p>
        </div>
        <div class="header-actions">
          <button class="btn-refresh" (click)="loadAreas()" [disabled]="loading()">
            <span>🔄</span> Actualizar
          </button>
          <a routerLink="/areas-trabajo/nuevo" class="btn-primary">
            <span>➕</span> Nueva Área
          </a>
        </div>
      </div>

      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input
          type="text"
          placeholder="Buscar por nombre..."
          (input)="onSearch($event)"
        />
      </div>

      @if (loading()) {
        <div class="loading-state">
          <div class="spinner-lg"></div>
          <p>Cargando áreas...</p>
        </div>
      }

      @if (error()) {
        <div class="error-state">
          <span class="error-icon">⚠️</span>
          <p>{{ error() }}</p>
          <button class="btn-retry" (click)="loadAreas()">Reintentar</button>
        </div>
      }

      @if (!loading() && !error() && filteredAreas().length === 0) {
        <div class="empty-state">
          <span class="empty-icon">🏢</span>
          <h3>No hay áreas registradas</h3>
          <p>Crea la primera área de trabajo en el sistema.</p>
          <a routerLink="/areas-trabajo/nuevo" class="btn-primary">Crear Área</a>
        </div>
      }

      @if (!loading() && !error() && filteredAreas().length > 0) {
        <div class="table-container">
          <table class="areas-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Tipo de Cultivo</th>
                <th>Estado</th>
                <th>Fecha Registro</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              @for (area of filteredAreas(); track area.id) {
                <tr>
                  <td class="name-cell">{{ area.nombre }}</td>
                  <td>{{ area.cultivoTipo || '—' }}</td>
                  <td>
                    @if (area.activo) {
                      <span class="status-active">Activo</span>
                    } @else {
                      <span class="status-inactive">Inactivo</span>
                    }
                  </td>
                  <td class="date-cell">{{ area.fechaRegistro | date:'dd/MM/yyyy' }}</td>
                  <td class="actions-cell">
                    <button class="btn-icon" title="Editar" (click)="editarArea(area)">
                      ✏️
                    </button>
                    <button class="btn-icon" title="Eliminar" (click)="confirmarEliminar(area)">
                      🗑️
                    </button>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <div class="table-footer">
          <span>Mostrando {{ filteredAreas().length }} de {{ areas().length }} áreas</span>
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
          <p>¿Estás seguro de eliminar el área <strong>{{ areaToDelete?.nombre }}</strong>?</p>
          <p class="modal-warning">Esta acción no se puede deshacer.</p>
          <div class="modal-actions">
            <button class="btn-cancel" (click)="showDeleteModal.set(false)">Cancelar</button>
            <button class="btn-danger" (click)="eliminarArea()" [disabled]="deleting()">
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
  `,
  styles: [`
    .page-container { max-width: 1000px; margin: 0 auto; }
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
      padding: 0.7rem 1.25rem; background: white; color: #555;
      border: 2px solid #e0e0e0; border-radius: 8px;
      font-size: 0.9rem; font-weight: 500; cursor: pointer; transition: all 0.2s;
    }
    .btn-refresh:hover:not(:disabled) { border-color: #667eea; color: #667eea; }
    .btn-refresh:disabled { opacity: 0.6; cursor: not-allowed; }

    .search-box {
      display: flex; align-items: center; gap: 0.5rem;
      background: white; padding: 0.6rem 1rem; border-radius: 10px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04); margin-bottom: 1.25rem;
    }
    .search-box input {
      border: none; background: transparent; outline: none;
      flex: 1; font-size: 0.9rem;
    }

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
    .btn-retry { margin-top: 1rem; padding: 0.6rem 1.5rem; background: #667eea; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: 500; }

    .table-container {
      background: white; border-radius: 12px; overflow: hidden;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
    .areas-table { width: 100%; border-collapse: collapse; }
    .areas-table th {
      padding: 0.85rem 1rem; text-align: left;
      font-size: 0.8rem; font-weight: 600; color: #888;
      text-transform: uppercase; letter-spacing: 0.5px;
      background: #fafafa; border-bottom: 2px solid #f0f0f0;
    }
    .areas-table td {
      padding: 0.75rem 1rem; font-size: 0.9rem; color: #333;
      border-bottom: 1px solid #f0f0f0;
    }
    .areas-table tr:hover { background: #f8f9ff; }
    .areas-table tr:last-child td { border-bottom: none; }
    .name-cell { font-weight: 600; color: #1a1a2e; }
    .date-cell { color: #666; font-size: 0.85rem; }

    .status-active { background: #ecfdf5; color: #059669; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; }
    .status-inactive { background: #fef2f2; color: #dc2626; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; }

    .actions-cell { display: flex; gap: 0.5rem; }
    .btn-icon { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; border: none; background: transparent; border-radius: 6px; cursor: pointer; font-size: 1rem; transition: background 0.2s; }
    .btn-icon:hover { background: #f0f0f0; }

    .table-footer { padding: 0.75rem 1rem; font-size: 0.85rem; color: #888; border-top: 1px solid #f0f0f0; background: #fafafa; }

    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; animation: fadeIn 0.2s; }
    @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    .modal-content { background: white; border-radius: 16px; padding: 2rem; max-width: 440px; width: 90%; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
    .modal-header { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; }
    .modal-header h3 { margin: 0; color: #1a1a2e; }
    .modal-icon { font-size: 1.5rem; }
    .modal-warning { color: #e74c3c; font-size: 0.85rem; }
    .modal-actions { display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem; }
    .btn-cancel { padding: 0.6rem 1.25rem; border: 2px solid #e0e0e0; border-radius: 8px; background: white; cursor: pointer; font-weight: 500; color: #555; }
    .btn-danger { padding: 0.6rem 1.25rem; background: #dc2626; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; display: flex; align-items: center; gap: 0.5rem; }
    .btn-danger:hover:not(:disabled) { background: #b91c1c; }
    .btn-danger:disabled { opacity: 0.6; cursor: not-allowed; }
    .spinner-sm { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite; }
  `]
})
export class AreaListComponent implements OnInit {
  areas = signal<AreaTrabajo[]>([]);
  filteredAreas = signal<AreaTrabajo[]>([]);
  loading = signal(false);
  error = signal('');
  searchTerm = '';

  showDeleteModal = signal(false);
  areaToDelete: AreaTrabajo | null = null;
  deleting = signal(false);

  constructor(
    private areaService: AreaTrabajoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAreas();
  }

  loadAreas(): void {
    this.loading.set(true);
    this.error.set('');

    this.areaService.getAll().subscribe({
      next: (data) => {
        this.areas.set(data);
        this.applyFilter();
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Error al cargar áreas: ' + (err.error?.message || err.message));
        this.loading.set(false);
      }
    });
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    this.applyFilter();
  }

  private applyFilter(): void {
    if (!this.searchTerm) {
      this.filteredAreas.set(this.areas());
      return;
    }
    this.filteredAreas.set(
      this.areas().filter(a => a.nombre.toLowerCase().includes(this.searchTerm))
    );
  }

  editarArea(area: AreaTrabajo): void {
    this.router.navigate(['/areas-trabajo/editar', area.id]);
  }

  confirmarEliminar(area: AreaTrabajo): void {
    this.areaToDelete = area;
    this.showDeleteModal.set(true);
  }

  eliminarArea(): void {
    if (!this.areaToDelete?.id) return;
    this.deleting.set(true);

    this.areaService.delete(this.areaToDelete.id).subscribe({
      next: () => {
        this.showDeleteModal.set(false);
        this.deleting.set(false);
        this.areaToDelete = null;
        this.loadAreas();
      },
      error: (err) => {
        this.deleting.set(false);
        alert('Error al eliminar área: ' + (err.error?.message || err.message));
      }
    });
  }
}
