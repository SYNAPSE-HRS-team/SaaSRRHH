import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { TareaAsignadaResponse } from '../../core/models/tarea-asignada.model';
import { ReporteDiarioRequest, ReporteDiarioResponse } from '../../core/models/reporte-diario.model';
import { EmpleadoResponse } from '../../core/models/empleado.model';
import { EmpleadoService } from '../../core/services/empleado.service';
import { TareaAsignadaService } from '../../core/services/tarea-asignada.service';
import { ReporteDiarioService } from '../../core/services/reporte-diario.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-reportes-diarios',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './reportes-diarios.component.html',
  styleUrls: ['./reportes-diarios.component.scss'],
})
export class ReportesDiariosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private reporteService = inject(ReporteDiarioService);
  private empleadoService = inject(EmpleadoService);
  private tareaService = inject(TareaAsignadaService);

  // signals
  reportes = signal<ReporteDiarioResponse[]>([]);
  reportesFiltrados = signal<ReporteDiarioResponse[]>([]);
  tareas = signal<TareaAsignadaResponse[]>([]);
  empleados = signal<EmpleadoResponse[]>([]);
  
  loading = signal(false);
  error = signal('');
  message = signal('');

  isEmployee = false;
  isAdmin = false;
  currentEmpleadoId?: number;

  // modal states
  isModalOpen = signal(false);
  editMode = signal(false);
  reporteSeleccionado = signal<ReporteDiarioResponse | null>(null);
  reporteForm!: FormGroup;

  // filters
  filtroEstado = signal('');
  filtroEmpleadoId = signal<number | null>(null);
  filtroFecha = signal('');

  ngOnInit(): void {
    const role = this.authService.getCurrentUser()?.rol;
    this.isEmployee = role === 'EMPLEADO' || role === 'TRABAJADOR';
    this.isAdmin = role === 'ADMIN';

    this.initForm();

    if (this.isEmployee) {
      const userId = this.authService.getCurrentUser()?.idUsuario;
      if (userId) {
        this.empleadoService.buscarPorUsuarioId(userId).subscribe({
          next: emp => {
            this.currentEmpleadoId = emp.id;
            this.loadReportes();
            this.loadEmployeeTasks();
          },
          error: err => {
            this.error.set('No se pudo encontrar tu ficha de empleado.');
          }
        });
      }
    } else {
      // Admin/Supervisor: load all lookups and list all reports
      this.loadEmployees();
      this.loadAllTasks();
      this.loadReportes();
    }
  }

  initForm(): void {
    this.reporteForm = this.fb.group({
      id: [null],
      tareaId: [null, Validators.required],
      empleadoId: [null],
      descripcionTrabajador: ['', [Validators.required, Validators.maxLength(1000)]],
      observacionSupervisor: ['', [Validators.maxLength(1000)]],
      porcentajeAvance: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      estado: ['PENDIENTE', Validators.required]
    });
  }

  loadReportes(): void {
    this.loading.set(true);
    const req = (this.isEmployee && this.currentEmpleadoId)
      ? this.reporteService.getReportesByEmpleado(this.currentEmpleadoId)
      : this.reporteService.listar();

    req.subscribe({
      next: data => {
        // Sort newest first
        const sorted = data.sort((a, b) => {
          const tA = a.fechaReporte ? new Date(a.fechaReporte).getTime() : 0;
          const tB = b.fechaReporte ? new Date(b.fechaReporte).getTime() : 0;
          return tB - tA;
        });
        this.reportes.set(sorted);
        this.aplicarFiltros();
        this.loading.set(false);
      },
      error: err => {
        this.error.set('Error al cargar reportes diarios.');
        this.loading.set(false);
      }
    });
  }

  loadEmployeeTasks(): void {
    if (!this.currentEmpleadoId) return;
    this.tareaService.getTareasByEmpleado(this.currentEmpleadoId).subscribe({
      next: data => {
        // En empleados solo mostramos tareas en progreso o pendientes
        const activeTasks = data.filter(t => t.estado === 'PENDIENTE' || t.estado === 'EN_PROGRESO');
        this.tareas.set(activeTasks);
      }
    });
  }

  loadEmployees(): void {
    this.empleadoService.listarActivos().subscribe({
      next: data => this.empleados.set(data)
    });
  }

  loadAllTasks(): void {
    this.tareaService.listar().subscribe({
      next: data => this.tareas.set(data)
    });
  }

  getEmpleadoNombre(id?: number): string {
    if (!id) return '—';
    const emp = this.empleados().find(e => e.id === id);
    return emp ? `${emp.apellidos}, ${emp.nombres}` : `Empleado #${id}`;
  }

  getEmpleadoDni(id?: number): string {
    if (!id) return '—';
    const emp = this.empleados().find(e => e.id === id);
    return emp ? emp.dni : '—';
  }

  getTareaLabel(id?: number): string {
    if (!id) return '—';
    const t = this.tareas().find(x => x.id === id);
    return t ? `[#${t.id}] ${t.funcion}` : `Tarea #${id}`;
  }

  getTareaDesc(id?: number): string {
    if (!id) return '';
    const t = this.tareas().find(x => x.id === id);
    return t ? t.descripcion || '' : '';
  }

  aplicarFiltros(): void {
    let result = [...this.reportes()];

    const state = this.filtroEstado();
    if (state) {
      result = result.filter(r => r.estado === state);
    }

    const empId = this.filtroEmpleadoId();
    if (empId !== null) {
      result = result.filter(r => r.empleadoId === empId);
    }

    const dateVal = this.filtroFecha();
    if (dateVal) {
      result = result.filter(r => r.fechaReporte?.startsWith(dateVal));
    }

    this.reportesFiltrados.set(result);
  }

  limpiarFiltros(): void {
    this.filtroEstado.set('');
    this.filtroEmpleadoId.set(null);
    this.filtroFecha.set('');
    this.aplicarFiltros();
  }

  abrirCrear(): void {
    if (!this.isEmployee) return;
    this.editMode.set(false);
    this.reporteSeleccionado.set(null);
    this.reporteForm.reset({
      tareaId: null,
      empleadoId: this.currentEmpleadoId,
      descripcionTrabajador: '',
      observacionSupervisor: '',
      porcentajeAvance: 0,
      estado: 'PENDIENTE'
    });
    this.isModalOpen.set(true);
  }

  abrirDetalleReview(reporte: ReporteDiarioResponse): void {
    this.editMode.set(true);
    this.reporteSeleccionado.set(reporte);
    this.reporteForm.patchValue({
      id: reporte.id,
      tareaId: reporte.tareaId,
      empleadoId: reporte.empleadoId,
      descripcionTrabajador: reporte.descripcionTrabajador,
      observacionSupervisor: reporte.observacionSupervisor || '',
      porcentajeAvance: reporte.porcentajeAvance,
      estado: reporte.estado
    });
    this.isModalOpen.set(true);
  }

  onSubmit(): void {
    if (this.reporteForm.invalid) return;

    const data: ReporteDiarioRequest = this.reporteForm.value;
    
    // Si es empleado, forzar su empleadoId
    if (this.isEmployee && this.currentEmpleadoId) {
      data.empleadoId = this.currentEmpleadoId;
    }

    const id = data.id;

    if (id) {
      // Actualizar reporte
      this.reporteService.actualizar(id, data).subscribe({
        next: () => {
          this.message.set('Reporte diario actualizado correctamente.');
          this.error.set('');
          this.isModalOpen.set(false);
          this.loadReportes();
        },
        error: err => {
          this.error.set(err.error?.message || 'Error al actualizar reporte diario.');
        }
      });
    } else {
      // Crear nuevo reporte
      this.reporteService.crear(data).subscribe({
        next: () => {
          this.message.set('Reporte diario creado correctamente.');
          this.error.set('');
          this.isModalOpen.set(false);
          this.loadReportes();
        },
        error: err => {
          this.error.set(err.error?.message || 'Error al enviar reporte diario.');
        }
      });
    }
  }

  eliminarReporte(id: number): void {
    if (!confirm('¿Estás seguro de eliminar este reporte diario?')) return;
    this.reporteService.eliminar(id).subscribe({
      next: () => {
        this.message.set('Reporte diario eliminado.');
        this.loadReportes();
      },
      error: err => this.error.set('No se pudo eliminar el reporte.')
    });
  }

  // Helper count getters
  get totalReportes(): number { return this.reportes().length; }
  get reportesValidados(): number { return this.reportes().filter(r => r.estado === 'VALIDADO').length; }
  get reportesPendientes(): number { return this.reportes().filter(r => r.estado === 'PENDIENTE').length; }
  get reportesObservados(): number { return this.reportes().filter(r => r.estado === 'OBSERVADO').length; }
}
