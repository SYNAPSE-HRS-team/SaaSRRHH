import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TareaAsignadaResponse } from '../../../../core/models/tarea-asignada.model';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';
import { TareaFormComponent } from '../tarea-form/tarea-form.component';

@Component({
  selector: 'app-tarea-list',
  standalone: true,
  imports: [CommonModule, TareaFormComponent],
  styleUrls: ['./tarea-list.component.scss'],
  template: `
    <div class="tareas-page">
      <div class="dashboard-header">
        <div class="header-text">
          <h1>Seguimiento de Tareas</h1>
          <p class="subtitle">Gestión y asignación de actividades operativas en el SaaS</p>
        </div>
        <div class="header-actions">
          <button
            (click)="marcarVencidas()"
            class="btn-warning"
            title="Marcar tareas vencidas como INCONCLUSO"
          >
            ⏰ Marcar Vencidas
          </button>
          <button (click)="abrirModal()" class="btn-create-task">+ Asignar Tarea</button>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="loading-spinner">
        <div class="spinner"></div>
        <p>Cargando tareas...</p>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="error-message">
        <p>❌ Error al cargar las tareas</p>
        <button (click)="cargarTareas()" class="btn-retry">Reintentar</button>
      </div>

      <!-- Tabla -->
      <div *ngIf="!loading && !error" class="table-wrapper">
        <table class="tasks-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Detalles de Tarea</th>
              <th>Responsable</th>
              <th>Función</th>
              <th>Estado</th>
              <th>Fecha</th>
              <th class="actions-header">Opciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngIf="tareas.length === 0">
              <td colspan="7" class="text-center">No hay tareas asignadas</td>
            </tr>
            <tr *ngFor="let t of tareas">
              <td class="id-cell">#{{ t.id }}</td>
              <td>
                <div class="task-title">{{ t.funcion }}</div>
                <div class="task-desc">{{ t.descripcion || 'Sin descripción' }}</div>
              </td>
              <td class="emp-cell">{{ t.empleado?.nombres || 'Sin asignar' }}</td>
              <td>
                <span class="badge-function">{{ t.funcion }}</span>
              </td>
              <td>
                <span
                  class="badge-status"
                  [ngClass]="{
                    'st-complete': t.estado === 'COMPLETADO',
                    'st-process': t.estado === 'EN_PROGRESO',
                    'st-pending': t.estado === 'PENDIENTE',
                    'st-canceled': t.estado === 'CANCELADO',
                    'st-inconcluso': t.estado === 'INCONCLUSO',
                  }"
                >
                  {{ t.estado }}
                </span>
              </td>
              <td class="date-cell">{{ t.fecha | date: 'dd/MM/yyyy' }}</td>
              <td class="actions-cell">
                <button (click)="editarTarea(t)" class="action-btn edit">✏️</button>
                <button (click)="eliminarTarea(t.id)" class="action-btn delete">🗑️</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <app-tarea-form
        *ngIf="isModalOpen"
        [tareaData]="tareaSeleccionada"
        [editMode]="editMode"
        (onCerrar)="cerrarModal()"
        (onExito)="alGuardarExitoso()"
      ></app-tarea-form>
    </div>
  `,
  styles: [
    `
      .tareas-page {
        padding: 24px;
      }
      .dashboard-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;
      }
      .header-text h1 {
        margin: 0;
        font-size: 1.5rem;
        color: #1a1a2e;
      }
      .header-text .subtitle {
        color: #6b7280;
        margin: 4px 0 0;
      }
      .header-actions {
        display: flex;
        gap: 12px;
      }
      .btn-create-task {
        background: #667eea;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 600;
      }
      .btn-create-task:hover {
        background: #5466ca;
      }
      .btn-warning {
        background: #f59e0b;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 600;
      }
      .btn-warning:hover {
        background: #d97706;
      }
      .loading-spinner {
        text-align: center;
        padding: 40px;
      }
      .spinner {
        border: 4px solid #e5e7eb;
        border-top: 4px solid #667eea;
        border-radius: 50%;
        width: 40px;
        height: 40px;
        animation: spin 1s linear infinite;
        margin: 0 auto 15px;
      }
      @keyframes spin {
        0% {
          transform: rotate(0deg);
        }
        100% {
          transform: rotate(360deg);
        }
      }
      .error-message {
        text-align: center;
        padding: 40px;
        background: #fef2f2;
        border-radius: 8px;
        border: 1px solid #fecaca;
        color: #dc2626;
      }
      .btn-retry {
        margin-top: 10px;
        padding: 8px 20px;
        background: #dc2626;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
      }
      .table-wrapper {
        background: white;
        border-radius: 10px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        overflow-x: auto;
        border: 1px solid #e2e8f0;
      }
      .tasks-table {
        width: 100%;
        border-collapse: collapse;
      }
      .tasks-table th {
        background: #f1f5f9;
        padding: 12px 16px;
        text-align: left;
        font-weight: 600;
        color: #475569;
        font-size: 0.85rem;
        border-bottom: 2px solid #e2e8f0;
      }
      .tasks-table td {
        padding: 12px 16px;
        border-bottom: 1px solid #e2e8f0;
      }
      .tasks-table tr:hover {
        background: #f8fafc;
      }
      .id-cell {
        font-weight: 600;
        color: #1a1a2e;
      }
      .task-title {
        font-weight: 500;
        color: #1a1a2e;
      }
      .task-desc {
        font-size: 0.8rem;
        color: #64748b;
        margin-top: 2px;
      }
      .emp-cell {
        font-weight: 500;
      }
      .badge-function {
        background: #e0e7ff;
        color: #4338ca;
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 0.75rem;
        font-weight: 600;
      }
      .badge-status {
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 0.75rem;
        font-weight: 600;
      }
      .st-complete {
        background: #dcfce7;
        color: #15803d;
      }
      .st-process {
        background: #dbeafe;
        color: #1d4ed8;
      }
      .st-pending {
        background: #fef3c7;
        color: #92400e;
      }
      .st-canceled {
        background: #fee2e2;
        color: #991b1b;
      }
      .st-inconcluso {
        background: #fef3c7;
        color: #92400e;
      }
      .date-cell {
        color: #475569;
      }
      .actions-cell {
        display: flex;
        gap: 8px;
      }
      .action-btn {
        background: none;
        border: none;
        cursor: pointer;
        font-size: 1.1rem;
        padding: 4px 8px;
        border-radius: 4px;
        transition: all 0.2s;
      }
      .action-btn.edit:hover {
        background: #e0e7ff;
      }
      .action-btn.delete:hover {
        background: #fee2e2;
      }
      .text-center {
        text-align: center;
        color: #6b7280;
        padding: 24px;
      }
    `,
  ],
})
export class TareaListComponent implements OnInit {
  tareas: TareaAsignadaResponse[] = [];
  isModalOpen = false;
  editMode = false;
  tareaSeleccionada: TareaAsignadaResponse | null = null;
  loading = false;
  error = false;

  constructor(private tareaService: TareaAsignadaService) {}

  ngOnInit(): void {
    this.cargarTareas();
  }

  cargarTareas(): void {
    this.loading = true;
    this.tareaService.listar().subscribe({
      next: (data: TareaAsignadaResponse[]) => {
        this.tareas = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error al cargar tareas:', err);
        this.error = true;
        this.loading = false;
      },
    });
  }

  // ✅ Marcar tareas vencidas manualmente
  marcarVencidas(): void {
    if (!confirm('¿Estás seguro de marcar todas las tareas vencidas como INCONCLUSO?')) {
      return;
    }
    this.tareaService.marcarVencidas().subscribe({
      next: () => {
        alert('✅ Tareas vencidas marcadas como INCONCLUSO');
        this.cargarTareas();
      },
      error: (err: any) => {
        console.error('Error al marcar vencidas:', err);
        alert('❌ Error al marcar tareas vencidas');
      },
    });
  }

  abrirModal(): void {
    this.editMode = false;
    this.tareaSeleccionada = null;
    this.isModalOpen = true;
  }

  editarTarea(tarea: TareaAsignadaResponse): void {
    this.editMode = true;
    this.tareaSeleccionada = tarea;
    this.isModalOpen = true;
  }

  cerrarModal(): void {
    this.isModalOpen = false;
    this.tareaSeleccionada = null;
  }

  alGuardarExitoso(): void {
    this.cargarTareas();
    this.cerrarModal();
  }

  eliminarTarea(id: number | undefined): void {
    if (!id) return;
    if (confirm(`¿Estás seguro de eliminar esta tarea?`)) {
      this.tareaService.eliminar(id).subscribe({
        next: () => {
          this.cargarTareas();
        },
        error: (err: any) => {
          console.error('Error al eliminar:', err);
          alert('Error al eliminar la tarea');
        },
      });
    }
  }
}
