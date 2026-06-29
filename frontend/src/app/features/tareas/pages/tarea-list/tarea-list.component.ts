import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TareaAsignadaResponse } from '../../../../core/models/tarea-asignada.model';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';
import { TareaFormComponent } from '../tarea-form/tarea-form.component';

@Component({
  selector: 'app-tarea-list',
  standalone: true,
  imports: [CommonModule, TareaFormComponent],
  templateUrl: './tarea-list.component.html',
  styleUrls: ['./tarea-list.component.scss']
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