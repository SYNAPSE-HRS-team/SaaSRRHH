import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EstadoTarea, TareaAsignadaResponse } from '../../../../core/models/tarea-asignada.model';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';
import { TareaFormComponent } from '../tarea-form/tarea-form.component';

@Component({
  selector: 'app-tarea-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TareaFormComponent],
  templateUrl: './tarea-list.component.html',
  styleUrls: ['./tarea-list.component.scss'],
})
export class TareaListComponent implements OnInit {
  // ===================================
  // DATOS
  // ===================================
  tareas: TareaAsignadaResponse[] = [];
  tareasFiltradas: TareaAsignadaResponse[] = [];

  isModalOpen = false;
  editMode = false;
  soloLectura = false;
  tareaSeleccionada: TareaAsignadaResponse | null = null;

  loading = false;
  error = false;

  // ===================================
  // FILTROS
  // ===================================
  filtroEstado: string = '';
  filtroEmpleadoId: number | null = null;
  filtroSupervisorId: number | null = null;
  filtroFecha: string = '';
  filtroFuncion: string = '';

  estadosFiltro = ['', ...Object.values(EstadoTarea)];
  funcionesDisponibles: string[] = [];

  // ✅ LISTAS DE EMPLEADOS Y SUPERVISORES (se llenan desde las tareas)
  empleadosDisponibles: any[] = [];
  supervisoresDisponibles: any[] = [];

  // ✅ Estados que bloquean la edición
  estadosBloqueados = ['COMPLETADO', 'CANCELADO', 'INCONCLUSO'];

  constructor(
    private tareaService: TareaAsignadaService,
    private empleadoService: EmpleadoService,
  ) {}

  ngOnInit(): void {
    this.cargarTareas();
    this.cargarEmpleados();
  }

  // ===================================
  // CRUD
  // ===================================

  cargarTareas(): void {
    this.loading = true;
    this.tareaService.listar().subscribe({
      next: (data: TareaAsignadaResponse[]) => {
        this.tareas = data;

        // ✅ Extraer funciones únicas para el filtro
        this.funcionesDisponibles = [...new Set(data.map((t) => t.funcion).filter(Boolean))];

        // ✅ Extraer empleados y supervisores de las tareas
        this.extraerEmpleadosYSupervisores(data);

        this.aplicarFiltros();
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error al cargar tareas:', err);
        this.error = true;
        this.loading = false;
      },
    });
  }

  // ✅ EXTRAER EMPLEADOS Y SUPERVISORES DESDE LAS TAREAS
  extraerEmpleadosYSupervisores(tareas: TareaAsignadaResponse[]): void {
    // Mapa para empleados (evitar duplicados)
    const empleadosMap = new Map<number, any>();
    const supervisoresMap = new Map<number, any>();

    tareas.forEach((t) => {
      // ✅ Extraer empleado
      if (t.empleado && t.empleado.id && !empleadosMap.has(t.empleado.id)) {
        empleadosMap.set(t.empleado.id, {
          id: t.empleado.id,
          nombres: t.empleado.nombres || '',
          apellidos: t.empleado.apellidos || '',
          dni: t.empleado.dni || '',
        });
      }

      // ✅ Extraer supervisor
      if (t.supervisor && t.supervisor.id && !supervisoresMap.has(t.supervisor.id)) {
        supervisoresMap.set(t.supervisor.id, {
          id: t.supervisor.id,
          nombres: t.supervisor.nombres || '',
          apellidos: t.supervisor.apellidos || '',
        });
      }
    });

    this.empleadosDisponibles = Array.from(empleadosMap.values());
    this.supervisoresDisponibles = Array.from(supervisoresMap.values());

    console.log('✅ Empleados extraídos:', this.empleadosDisponibles);
    console.log('✅ Supervisores extraídos:', this.supervisoresDisponibles);
  }

  // ✅ CARGAR EMPLEADOS DESDE EL BACKEND (como fallback)
  cargarEmpleados(): void {
    // Solo si no hay empleados en las tareas, cargar desde el backend
    if (this.empleadosDisponibles.length === 0) {
      this.empleadoService.listarActivos().subscribe({
        next: (data: any[]) => {
          this.empleadosDisponibles = data;
          console.log('✅ Empleados cargados desde backend:', this.empleadosDisponibles);
        },
        error: (err: any) => {
          console.error('❌ Error al cargar empleados:', err);
        },
      });
    }

    // Solo si no hay supervisores en las tareas, cargar desde el backend
    if (this.supervisoresDisponibles.length === 0) {
      this.empleadoService.listarSupervisoresByRol().subscribe({
        next: (data: any[]) => {
          this.supervisoresDisponibles = data;
          console.log('✅ Supervisores cargados desde backend:', this.supervisoresDisponibles);
        },
        error: (err: any) => {
          console.error('❌ Error al cargar supervisores:', err);
        },
      });
    }
  }

  abrirModal(): void {
    this.editMode = false;
    this.soloLectura = false;
    this.tareaSeleccionada = null;
    this.isModalOpen = true;
  }

  verTarea(tarea: TareaAsignadaResponse): void {
    this.editMode = false;
    this.soloLectura = true;
    this.tareaSeleccionada = tarea;
    this.isModalOpen = true;
  }

  editarTarea(tarea: TareaAsignadaResponse): void {
    if (!this.puedeEditar(tarea.estado)) {
      alert(`❌ La tarea está ${tarea.estado} y no se puede modificar.`);
      return;
    }
    this.editMode = true;
    this.soloLectura = false;
    this.tareaSeleccionada = tarea;
    this.isModalOpen = true;
  }

  cerrarModal(): void {
    this.isModalOpen = false;
    this.tareaSeleccionada = null;
    this.soloLectura = false;
  }

  alGuardarExitoso(): void {
    this.cargarTareas();
    this.cerrarModal();
  }

  eliminarTarea(id: number | undefined): void {
    if (!id) return;
    if (confirm('¿Estás seguro de eliminar esta tarea?')) {
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

  // ===================================
  // FILTROS
  // ===================================

  aplicarFiltros(): void {
    let resultado = [...this.tareas];

    if (this.filtroEstado) {
      resultado = resultado.filter((t) => t.estado === this.filtroEstado);
    }

    if (this.filtroEmpleadoId) {
      resultado = resultado.filter((t) => t.empleado?.id === this.filtroEmpleadoId);
    }

    if (this.filtroSupervisorId) {
      resultado = resultado.filter((t) => t.supervisor?.id === this.filtroSupervisorId);
    }

    if (this.filtroFecha) {
      resultado = resultado.filter((t) => {
        if (!t.fecha) return false;
        const fechaTarea = new Date(t.fecha).toISOString().split('T')[0];
        return fechaTarea === this.filtroFecha;
      });
    }

    if (this.filtroFuncion) {
      resultado = resultado.filter((t) => t.funcion === this.filtroFuncion);
    }

    this.tareasFiltradas = resultado;
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroEmpleadoId = null;
    this.filtroSupervisorId = null;
    this.filtroFecha = '';
    this.filtroFuncion = '';
    this.aplicarFiltros();
  }

  // ===================================
  // UTILIDADES
  // ===================================

  puedeEditar(estado: string): boolean {
    return !this.estadosBloqueados.includes(estado);
  }

  // ===================================
  // CONTADORES POR ESTADO
  // ===================================

  get totalTareas(): number {
    return this.tareas.length;
  }

  get tareasPendientes(): number {
    return this.tareas.filter((t) => t.estado === 'PENDIENTE').length;
  }

  get tareasEnProgreso(): number {
    return this.tareas.filter((t) => t.estado === 'EN_PROGRESO').length;
  }

  get tareasCompletadas(): number {
    return this.tareas.filter((t) => t.estado === 'COMPLETADO').length;
  }

  get tareasCanceladas(): number {
    return this.tareas.filter((t) => t.estado === 'CANCELADO').length;
  }

  get tareasInconclusas(): number {
    return this.tareas.filter((t) => t.estado === 'INCONCLUSO').length;
  }
}
