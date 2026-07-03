import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EstadoTarea, TareaAsignadaResponse } from '../../../../core/models/tarea-asignada.model';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';
import { TareaFormComponent } from '../tarea-form/tarea-form.component';
import { AuthService } from '../../../../core/services/auth.service';

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
  isEmployee = false;
  isAdmin = false;
  currentEmpleadoId?: number;
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

  // ✅ LISTAS DE EMPLEADOS Y SUPERVISORES (cargadas desde el backend)
  empleadosDisponibles: any[] = [];
  supervisoresDisponibles: any[] = [];

  // ✅ Estados que bloquean la edición
  estadosBloqueados = ['COMPLETADO', 'CANCELADO', 'INCONCLUSO'];

  constructor(
    private authService: AuthService,
    private tareaService: TareaAsignadaService,
    private empleadoService: EmpleadoService,
  ) {}

  ngOnInit(): void {
    const role = this.authService.getCurrentUser()?.rol;
    this.isEmployee = role === 'EMPLEADO' || role === 'TRABAJADOR';
    this.isAdmin = role === 'ADMIN';

    if (this.isEmployee) {
      const userId = this.authService.getCurrentUser()?.idUsuario;
      if (userId) {
        this.empleadoService.buscarPorUsuarioId(userId).subscribe({
          next: emp => {
            this.currentEmpleadoId = emp.id;
            this.cargarTareas();
          },
          error: err => {
            console.error('Error al cargar datos del empleado:', err);
            this.cargarTareas();
          }
        });
      } else {
        this.cargarTareas();
      }
    } else {
      this.cargarEmpleados(() => this.cargarSupervisores(() => this.cargarTareas()));
    }
  }

  // ===================================
  // CRUD
  // ===================================

  cargarTareas(): void {
    this.loading = true;
    const req = (this.isEmployee && this.currentEmpleadoId)
      ? this.tareaService.getTareasByEmpleado(this.currentEmpleadoId)
      : this.tareaService.listar();

    req.subscribe({
      next: (data: TareaAsignadaResponse[]) => {
        console.log('Tareas recibidas del backend:', data);
        this.tareas = data;
        // Enriquecer y normalizar tareas: maneja distintas formas de respuesta
        const normalizePerson = (p: any) => {
          if (!p) return undefined;
          const id = p.id ?? p.employeeId ?? p.empleadoId ?? null;
          const nombres = p.nombres ?? p.nombre ?? p.firstName ?? p.first_name ?? p.employeeFirstName ?? '';
          const apellidos = p.apellidos ?? p.apellido ?? p.lastName ?? p.last_name ?? p.employeeLastName ?? '';
          const dni = p.dni ?? p.documento ?? p.doc ?? null;
          return id ? { id, nombres: (nombres || '').toString().trim(), apellidos: (apellidos || '').toString().trim(), dni } : undefined;
        };

        const enrichPerson = (raw: any, lookup: any[]) => {
          const normalized = normalizePerson(raw);
          if (!normalized) return undefined;
          const hasNames = (normalized.nombres || '').toString().trim() || (normalized.apellidos || '').toString().trim();
          if (hasNames) return normalized;
          if (normalized.id) {
            const found = lookup.find((e) => e.id === normalized.id);
            return normalizePerson(found) || normalized;
          }
          return normalized;
        };

        this.tareas.forEach(t => {
          // empleado
          const rawEmpleado = (t as any).empleado || (t as any).employee || (t as any).empleadoResponse || null;
          t.empleado = enrichPerson(rawEmpleado || t.empleado, this.empleadosDisponibles) ||
            (t.empleadoId ? enrichPerson({ id: t.empleadoId }, this.empleadosDisponibles) : undefined) ||
            t.empleado;

          // supervisor
          const rawSupervisor = (t as any).supervisor || (t as any).supervisorResponse || null;
          t.supervisor = enrichPerson(rawSupervisor || t.supervisor, this.supervisoresDisponibles) ||
            (t.supervisorId ? enrichPerson({ id: t.supervisorId }, this.supervisoresDisponibles) : undefined) ||
            t.supervisor;
        });
        // ✅ Extraer funciones únicas para el filtro
        this.funcionesDisponibles = [...new Set(data.map((t) => t.funcion).filter(Boolean))];
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

  // ✅ CARGAR EMPLEADOS DESDE EL BACKEND
  cargarEmpleados(onComplete?: () => void): void {
    // ✅ Usar listarActivos() que ya existe en el servicio
    this.empleadoService.listarActivos().subscribe({
      next: (data: any[]) => {
        this.empleadosDisponibles = data;
        console.log('✅ Empleados cargados:', this.empleadosDisponibles);
        if (onComplete) onComplete();
      },
      error: (err: any) => {
        console.error('❌ Error al cargar empleados:', err);
        // ✅ Fallback: usar listarTrabajadores()
        this.empleadoService.listarTrabajadores().subscribe({
          next: (data: any[]) => {
            this.empleadosDisponibles = data;
            console.log('✅ Empleados cargados (fallback):', this.empleadosDisponibles);
            if (onComplete) onComplete();
          },
          error: (err2: any) => {
            console.error('❌ Error al cargar empleados (fallback):', err2);
            if (onComplete) onComplete();
          },
        });
      },
    });
  }

  // ✅ CARGAR SUPERVISORES DESDE EL BACKEND
  cargarSupervisores(onComplete?: () => void): void {
    // ✅ Usar listarSupervisoresByRol() que ya existe en el servicio
    this.empleadoService.listarSupervisoresByRol().subscribe({
      next: (data: any[]) => {
        this.supervisoresDisponibles = data;
        console.log('✅ Supervisores cargados:', this.supervisoresDisponibles);
        if (onComplete) onComplete();
      },
      error: (err: any) => {
        console.error('❌ Error al cargar supervisores:', err);
        // ✅ Fallback: usar listarSupervisores()
        this.empleadoService.listarSupervisores().subscribe({
          next: (data: any[]) => {
            this.supervisoresDisponibles = data;
            console.log('✅ Supervisores cargados (fallback):', this.supervisoresDisponibles);
            if (onComplete) onComplete();
          },
          error: (err2: any) => {
            console.error('❌ Error al cargar supervisores (fallback):', err2);
            if (onComplete) onComplete();
          },
        });
      },
    });
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

    if (this.filtroEmpleadoId !== null) {
      resultado = resultado.filter((t) => (t.empleado?.id ?? t.empleadoId) === this.filtroEmpleadoId);
    }

    if (this.filtroSupervisorId !== null) {
      resultado = resultado.filter((t) => (t.supervisor?.id ?? t.supervisorId) === this.filtroSupervisorId);
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

  actualizarEstado(id: number | undefined, nuevoEstado: string): void {
    if (!id) return;
    this.tareaService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => {
        this.cargarTareas();
      },
      error: (err: any) => {
        console.error('Error al cambiar estado:', err);
        alert('Error al actualizar el estado de la tarea.');
      }
    });
  }
}
