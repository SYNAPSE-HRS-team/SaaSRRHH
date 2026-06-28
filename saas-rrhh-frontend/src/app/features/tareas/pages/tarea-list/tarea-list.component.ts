import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TareaFormComponent } from '../tarea-form/tarea-form.component';
import { TareaAsignada } from '../../../../core/models/tarea-asignada.model'; // Tu modelo real

@Component({
  selector: 'app-tarea-list',
  standalone: true,
  imports: [CommonModule, TareaFormComponent],
  styleUrls: ['./tarea-list.component.scss'], // SASS externo
  template: `
    <div class="tareas-page">
      <div class="dashboard-header">
        <div class="header-text">
          <h1>Seguimiento de Tareas</h1>
          <p class="subtitle">Gestión y asignación de actividades operativas en el SaaS</p>
        </div>
        <button (click)="abrirModal()" class="btn-create-task">+ Asignar Tarea</button>
      </div>

      <div class="table-wrapper">
        <table class="tasks-table">
          <thead>
            <tr>
              <th>Código</th>
              <th>Detalles de Tarea</th>
              <th>Responsable</th>
              <th>Prioridad</th>
              <th>Estado</th>
              <th>Vencimiento</th>
              <th class="actions-header">Opciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let t of tareas">
              <td class="id-cell">#{{ t.idTarea }}</td>
              <td>
                <div class="task-title">{{ t.titulo }}</div>
                <div class="task-desc">{{ t.descripcion }}</div>
              </td>
              <td class="emp-cell">{{ t.empleado?.nombres || 'Sin asignar' }}</td>
              <td>
                <span class="badge-priority" [ngClass]="{
                  'p-alta': t.prioridad === 'ALTA',
                  'p-media': t.prioridad === 'MEDIA',
                  'p-baja': t.prioridad === 'BAJA'
                }">{{ t.prioridad }}</span>
              </td>
              <td>
                <span class="badge-status" [ngClass]="{
                  'st-complete': t.estado === 'COMPLETADA',
                  'st-process': t.estado === 'EN_PROCESO',
                  'st-pending': t.estado === 'PENDIENTE'
                }">{{ t.estado }}</span>
              </td>
              <td class="date-cell">{{ t.fechaVencimiento | date:'dd/MM/yyyy' }}</td>
              <td class="actions-cell">
                <button (click)="editarTarea(t)" class="action-btn edit">Modificar</button>
                <button (click)="eliminarTarea(t.idTarea)" class="action-btn delete">Remover</button>
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
  `
})
export class TareaListComponent implements OnInit {
  // Arreglo perfectamente tipado bajo tu interfaz estricta
  tareas: TareaAsignada[] = [
    { 
      idTarea: 1, 
      titulo: 'Revisión de Contratos', 
      descripcion: 'Auditar los nuevos contratos del área de TI.', 
      prioridad: 'ALTA', 
      estado: 'EN_PROCESO', 
      fechaVencimiento: '2026-07-10', 
      empleado: { id: 1, nombres: 'Carlos Mendoza' } as any 
    },
    { 
      idTarea: 2, 
      titulo: 'Actualizar Manual de Bienestar', 
      descripcion: 'Subir la nueva documentación técnica.', 
      prioridad: 'BAJA', 
      estado: 'PENDIENTE', 
      fechaVencimiento: '2026-07-25', 
      empleado: { id: 2, nombres: 'Ana Gómez' } as any 
    }
  ];
  
  isModalOpen = false;
  editMode = false;
  tareaSeleccionada: TareaAsignada | null = null;

  ngOnInit(): void {
    this.cargarTareas();
  }

  cargarTareas(): void {
    // Aquí irá el consumo de tu servicio de Tareas más adelante
  }

  abrirModal(): void {
    this.editMode = false;
    this.tareaSeleccionada = null;
    this.isModalOpen = true;
  }

  editarTarea(tarea: TareaAsignada): void {
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

  eliminarTarea(idTarea: number | undefined): void {
    if (!idTarea) return;
    if (confirm(`¿Estás seguro de que deseas eliminar la tarea #${idTarea}?`)) {
      // Lógica de eliminación usando idTarea
    }
  }
}