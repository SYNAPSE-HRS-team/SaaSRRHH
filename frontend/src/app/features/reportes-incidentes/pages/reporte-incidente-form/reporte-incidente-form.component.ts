import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
    EstadoIncidente,
    NivelRiesgo,
    ReporteIncidenteRequest,
    ReporteIncidenteResponse,
    TipoIncidente,
} from '../../../../core/models/reporte-incidente.model';
import { ReporteIncidenteService } from '../../../../core/services/reporte-incidente.service';
import { TareaAsignadaService } from '../../../../core/services/tarea-asignada.service';
import { TareaAsignadaResponse } from '../../../../core/models/tarea-asignada.model';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';

@Component({
  selector: 'app-reporte-incidente-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reporte-incidente-form.component.html',
  styleUrls: ['./reporte-incidente-form.component.scss'],
})
export class ReporteIncidenteFormComponent implements OnInit {
  form!: FormGroup;
  esEdicion = false;
  esVisualizacion = false;
  idIncidente?: number;
  loading = false;
  submitted = false;
  error = false;
  errorMessage = '';

  // ============================================
  // DATOS DE TAREAS
  // ============================================
  tareasOriginales: TareaAsignadaResponse[] = [];
  tareasFiltradas: TareaAsignadaResponse[] = [];
  
  tiposIncidente = Object.values(TipoIncidente);
  nivelesRiesgo = Object.values(NivelRiesgo);
  estadosIncidente = Object.values(EstadoIncidente);

  // ============================================
  // FILTROS
  // ============================================
  filtros = {
    fecha: '',
    estado: '',
    empleadoId: null as number | null,
    searchTerm: '',
  };

  opcionesFecha = [
    { value: 'hoy', label: '📅 Hoy' },
    { value: 'semana', label: '📅 Esta semana' },
    { value: 'mes', label: '📅 Este mes' },
    { value: 'todas', label: '📅 Todas' },
  ];

  opcionesEstado = [
    { value: '', label: '📊 Todos los estados' },
    { value: 'PENDIENTE', label: '⏳ Pendientes' },
    { value: 'EN_PROGRESO', label: '🔄 En progreso' },
    { value: 'COMPLETADO', label: '✅ Completadas' },
    { value: 'CANCELADO', label: '❌ Canceladas' },
    { value: 'INCONCLUSO', label: '⚠️ Inconclusas' },
  ];

  empleados: EmpleadoResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private reporteService: ReporteIncidenteService,
    private tareaService: TareaAsignadaService,
    private empleadoService: EmpleadoService,
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
    this.cargarEmpleados();
    this.cargarTareas();
  }

  inicializarFormulario(): void {
    this.form = this.fb.group({
      tareaId: ['', [Validators.required]],
      tipo: ['', [Validators.required]],
      descripcion: ['', [Validators.required, Validators.maxLength(1000)]],
      evidenciaUrl: ['', [Validators.maxLength(500)]],
      nivelRiesgo: [''],
      estado: [''],
      fechaIncidente: ['', [Validators.required]],
    });
  }

  // ============================================
  // CARGA DE DATOS
  // ============================================
  cargarEmpleados(): void {
    this.empleadoService.listarActivos().subscribe({
      next: (data) => {
        this.empleados = data;
      },
      error: (err) => {
        console.error('❌ Error cargando empleados:', err);
      },
    });
  }

  cargarTareas(): void {
    this.loading = true;
    this.tareaService.listar().subscribe({
      next: (data) => {
        this.tareasOriginales = data;
        this.aplicarFiltros();
        this.verificarParametros();
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Error al cargar tareas:', err);
        this.error = true;
        this.errorMessage = 'Error al cargar las tareas';
        this.loading = false;
      },
    });
  }

  verificarParametros(): void {
    this.route.params.subscribe((params) => {
      const id = params['id'];
      const url = this.router.url;

      if (url.includes('/ver/')) {
        this.esVisualizacion = true;
        this.idIncidente = +id;
        this.cargarReporte(this.idIncidente, true);
      } else if (url.includes('/editar/')) {
        this.esEdicion = true;
        this.idIncidente = +id;
        this.cargarReporte(this.idIncidente, false);
      } else {
        this.form.patchValue({
          fechaIncidente: new Date().toISOString().slice(0, 16),
        });
      }
    });
  }

  cargarReporte(id: number, soloLectura: boolean): void {
    this.loading = true;
    this.reporteService.obtenerPorId(id).subscribe({
      next: (data: ReporteIncidenteResponse) => {
        this.form.patchValue({
          tareaId: data.tareaId || '',
          tipo: data.tipo,
          descripcion: data.descripcion,
          evidenciaUrl: data.evidenciaUrl || '',
          nivelRiesgo: data.nivelRiesgo || '',
          estado: data.estado || '',
          fechaIncidente: new Date(data.fechaIncidente).toISOString().slice(0, 16),
        });

        if (soloLectura) {
          this.form.disable();
        }

        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error al cargar reporte:', err);
        this.error = true;
        this.errorMessage = 'No se pudo cargar el reporte';
        this.loading = false;
      },
    });
  }

  // ============================================
  // FILTROS
  // ============================================
  aplicarFiltros(): void {
    let resultado = [...this.tareasOriginales];

    // Filtro por fecha
    if (this.filtros.fecha !== 'todas') {
      const hoy = new Date();
      const hoyStr = hoy.toISOString().split('T')[0];
      
      if (this.filtros.fecha === 'hoy') {
        resultado = resultado.filter(t => {
          const fechaTarea = new Date(t.fecha).toISOString().split('T')[0];
          return fechaTarea === hoyStr;
        });
      } else if (this.filtros.fecha === 'semana') {
        const inicioSemana = new Date(hoy);
        inicioSemana.setDate(hoy.getDate() - hoy.getDay());
        const finSemana = new Date(inicioSemana);
        finSemana.setDate(inicioSemana.getDate() + 6);
        
        resultado = resultado.filter(t => {
          const fechaTarea = new Date(t.fecha);
          return fechaTarea >= inicioSemana && fechaTarea <= finSemana;
        });
      } else if (this.filtros.fecha === 'mes') {
        const inicioMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        const finMes = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0);
        
        resultado = resultado.filter(t => {
          const fechaTarea = new Date(t.fecha);
          return fechaTarea >= inicioMes && fechaTarea <= finMes;
        });
      }
    }

    // Filtro por estado
    if (this.filtros.estado) {
      resultado = resultado.filter(t => t.estado === this.filtros.estado);
    }

    // Filtro por empleado
    if (this.filtros.empleadoId) {
      resultado = resultado.filter(t => 
        t.empleado?.id === this.filtros.empleadoId ||
        t.empleadoId === this.filtros.empleadoId
      );
    }

    // Filtro por búsqueda
    if (this.filtros.searchTerm.trim()) {
      const term = this.filtros.searchTerm.toLowerCase().trim();
      resultado = resultado.filter(t =>
        t.funcion?.toLowerCase().includes(term) ||
        t.descripcion?.toLowerCase().includes(term) ||
        t.empleado?.nombres?.toLowerCase().includes(term) ||
        t.empleado?.apellidos?.toLowerCase().includes(term) ||
        t.supervisor?.nombres?.toLowerCase().includes(term) ||
        t.supervisor?.apellidos?.toLowerCase().includes(term) ||
        t.area?.nombre?.toLowerCase().includes(term)
      );
    }

    this.tareasFiltradas = resultado;
    
    // Si la tarea seleccionada ya no está en los filtros, limpiar selección
    const tareaSeleccionada = this.form.get('tareaId')?.value;
    if (tareaSeleccionada) {
      const existe = this.tareasFiltradas.some(t => t.id === Number(tareaSeleccionada));
      if (!existe) {
        this.form.patchValue({ tareaId: '' });
      }
    }
  }

  // ============================================
  // MANEJADORES DE FILTROS
  // ============================================
  onFiltroFechaChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.filtros.fecha = value;
    this.aplicarFiltros();
  }

  onFiltroEstadoChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.filtros.estado = value;
    this.aplicarFiltros();
  }

  onFiltroEmpleadoChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.filtros.empleadoId = value ? Number(value) : null;
    this.aplicarFiltros();
  }

  onSearchChange(event: Event): void {
    this.filtros.searchTerm = (event.target as HTMLInputElement).value;
    this.aplicarFiltros();
  }

  limpiarFiltros(): void {
    // Resetear todos los filtros a su valor por defecto
    this.filtros = {
      fecha: '',
      estado: '',
      empleadoId: null,
      searchTerm: '',
    };
    
    // Limpiar el input de búsqueda visualmente
    const searchInput = document.querySelector('.filtro-input') as HTMLInputElement;
    if (searchInput) {
      searchInput.value = '';
    }
    
    // Forzar actualización de los selects
    this.actualizarSelectsVisualmente();
    this.aplicarFiltros();
  }

  /**
   * Método auxiliar para actualizar los selects visualmente
   */
  private actualizarSelectsVisualmente(): void {
    // Select de fecha
    const fechaSelect = document.querySelector('.filtro-group select') as HTMLSelectElement;
    if (fechaSelect) {
      fechaSelect.value = this.filtros.fecha;
    }
    
    // Select de estado (buscamos el segundo select)
    const selects = document.querySelectorAll('.filtro-group select');
    if (selects.length >= 2) {
      const estadoSelect = selects[1] as HTMLSelectElement;
      if (estadoSelect) {
        estadoSelect.value = this.filtros.estado;
      }
    }
    
    // Select de empleado (buscamos el tercer select)
    if (selects.length >= 3) {
      const empleadoSelect = selects[2] as HTMLSelectElement;
      if (empleadoSelect) {
        empleadoSelect.value = this.filtros.empleadoId?.toString() || '';
      }
    }
  }

  // ============================================
  // GETTERS
  // ============================================
  get f() {
    return this.form.controls;
  }

  get selectedTarea(): TareaAsignadaResponse | undefined {
    const tareaId = this.form.get('tareaId')?.value;
    return this.tareasFiltradas.find((t) => t.id === Number(tareaId));
  }

  get hayTareasFiltradas(): boolean {
    return this.tareasFiltradas.length > 0;
  }

  get totalTareasFiltradas(): number {
    return this.tareasFiltradas.length;
  }

  get totalTareasOriginales(): number {
    return this.tareasOriginales.length;
  }

  // ============================================
  // UTILIDADES
  // ============================================
  getNombreCompleto(emp: any): string {
    if (!emp) return 'No asignado';
    return `${emp.nombres || ''} ${emp.apellidos || ''}`.trim() || 'No asignado';
  }

  getColorEstado(estado: string): string {
    const colores: { [key: string]: string } = {
      'PENDIENTE': '#ff9800',
      'EN_PROGRESO': '#2196f3',
      'COMPLETADO': '#4caf50',
      'CANCELADO': '#f44336',
      'INCONCLUSO': '#9e9e9e',
    };
    return colores[estado] || '#9e9e9e';
  }

  getIconoEstado(estado: string): string {
    const iconos: { [key: string]: string } = {
      'PENDIENTE': '⏳',
      'EN_PROGRESO': '🔄',
      'COMPLETADO': '✅',
      'CANCELADO': '❌',
      'INCONCLUSO': '⚠️',
    };
    return iconos[estado] || '📋';
  }

  // ============================================
  // ACCIONES DEL FORMULARIO
  // ============================================
  onSubmit(): void {
    this.submitted = true;
    this.error = false;

    if (this.form.invalid) {
      return;
    }

    const selectedTarea = this.selectedTarea;
    if (!selectedTarea) {
      this.error = true;
      this.errorMessage = 'Debe seleccionar una tarea válida';
      return;
    }

    this.loading = true;
    const dto: ReporteIncidenteRequest = {
      tareaId: selectedTarea.id,
      areaId: selectedTarea.area?.id || selectedTarea.areaId,
      empleadoId: selectedTarea.empleado?.id || selectedTarea.empleadoId || 0,
      supervisorId: selectedTarea.supervisor?.id || selectedTarea.supervisorId,
      tipo: this.form.value.tipo,
      descripcion: this.form.value.descripcion,
      evidenciaUrl: this.form.value.evidenciaUrl,
      nivelRiesgo: this.form.value.nivelRiesgo || 'BAJO',
      estado: this.form.value.estado || 'REPORTADO',
      fechaIncidente: this.form.value.fechaIncidente,
    };

    if (this.esEdicion && this.idIncidente) {
      this.actualizarReporte(this.idIncidente, dto);
    } else {
      this.crearReporte(dto);
    }
  }

  crearReporte(dto: ReporteIncidenteRequest): void {
    this.reporteService.crear(dto).subscribe({
      next: () => {
        this.router.navigate(['/reportes-incidentes']);
      },
      error: (err: any) => {
        console.error('Error al crear:', err);
        this.error = true;
        this.errorMessage = 'Error al crear el reporte';
        this.loading = false;
      },
    });
  }

  actualizarReporte(id: number, dto: ReporteIncidenteRequest): void {
    this.reporteService.actualizar(id, dto).subscribe({
      next: () => {
        this.router.navigate(['/reportes-incidentes']);
      },
      error: (err: any) => {
        console.error('Error al actualizar:', err);
        this.error = true;
        this.errorMessage = 'Error al actualizar el reporte';
        this.loading = false;
      },
    });
  }

  cancelar(): void {
    this.router.navigate(['/reportes-incidentes']);
  }

  getTitulo(): string {
    if (this.esVisualizacion) return 'Ver Reporte de Incidente';
    if (this.esEdicion) return 'Editar Reporte de Incidente';
    return 'Nuevo Reporte de Incidente';
  }
}