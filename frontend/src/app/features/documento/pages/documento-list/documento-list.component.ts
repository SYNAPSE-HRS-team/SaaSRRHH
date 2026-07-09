import { CommonModule } from '@angular/common';
import { Component, computed, OnDestroy, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  finalize,
  of,
  Subject,
  takeUntil,
} from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { DocumentoPrivadoResponse } from '../../../../core/models/documento-privado.model';
import { TipoDocumentoResponse } from '../../../../core/models/tipo-documento.model';
import { DocumentoService } from '../../../../core/services/documento.service';
import { TipoDocumentoService } from '../../../../core/services/tipo-documento.service';

@Component({
  selector: 'app-documento-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './documento-list.component.html',
  styleUrls: ['./documento-list.component.scss'],
})
export class DocumentoListComponent implements OnInit, OnDestroy {
  // Signals para estado reactivo
  documentos = signal<DocumentoPrivadoResponse[]>([]);
  documentosFiltrados = signal<DocumentoPrivadoResponse[]>([]);
  tiposDocumento = signal<TipoDocumentoResponse[]>([]);

  // Estados de carga
  cargando = signal(true);
  errorHttp = signal(false);

  // Filtros
  filtroBusqueda = signal('');
  filtroTipoId = signal<number | null>(null);
  filtroEstado = signal<string>('todos');
  filtroFechaDesde = signal<string>('');
  filtroFechaHasta = signal<string>('');

  // Ordenamiento
  ordenCampo = signal<string>('fechaCarga');
  ordenDireccion = signal<'asc' | 'desc'>('desc');

  // Estadísticas
  totalDocumentos = computed(() => this.documentosFiltrados().length);
  totalActivos = computed(() => this.documentos().filter((d) => d.activo).length);
  totalVencidos = computed(() => this.documentos().filter((d) => this.estaVencido(d)).length);

  // Manejo de destrucción
  private destroy$ = new Subject<void>();

  opcionesOrden = [
    { value: 'fechaCarga', label: 'Fecha de carga' },
    { value: 'fechaEmision', label: 'Fecha de emisión' },
    { value: 'fechaVencimiento', label: 'Fecha de vencimiento' },
    { value: 'empleadoNombre', label: 'Empleado' },
    { value: 'tipoNombre', label: 'Tipo' },
  ];

  constructor(
    private documentoService: DocumentoService,
    private tipoDocumentoService: TipoDocumentoService,
  ) {}

  ngOnInit(): void {
    this.cargarTiposDocumento();
    this.cargarDocumentos();
    this.configurarFiltros();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Carga los tipos de documento para los filtros
   */
  private cargarTiposDocumento(): void {
    this.tipoDocumentoService
      .getAll()
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al cargar tipos:', error);
          return of([]);
        }),
      )
      .subscribe({
        next: (tipos) => {
          this.tiposDocumento.set(tipos);
        },
      });
  }

  /**
   * Carga los documentos desde el servicio
   */
  cargarDocumentos(): void {
    this.cargando.set(true);
    this.errorHttp.set(false);

    this.documentoService
      .getAll()
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al conectar con la API:', error);
          this.errorHttp.set(true);
          this.cargando.set(false);
          return of([]);
        }),
        finalize(() => this.cargando.set(false)),
      )
      .subscribe({
        next: (data) => {
          console.log('📄 Documentos cargados:', data);
          // Verificar que cada documento tenga fechaEmision
          data.forEach((doc) => {
            console.log(`📅 Doc ${doc.id}: fechaEmision = ${doc.fechaEmision}`);
          });
          this.documentos.set(data);
          this.aplicarFiltros();
        },
      });
  }

  /**
   * Configura los filtros con debounce para mejor rendimiento
   */
  private configurarFiltros(): void {
    const busquedaSubject = new Subject<string>();

    busquedaSubject
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.aplicarFiltros();
      });

    const originalSet = this.filtroBusqueda.set.bind(this.filtroBusqueda);
    this.filtroBusqueda.set = (value: string) => {
      originalSet(value);
      busquedaSubject.next(value);
    };
  }

  /**
   * Aplica todos los filtros y ordenamiento
   */
  aplicarFiltros(): void {
    let filtrados = [...this.documentos()];

    // Filtro por búsqueda
    const busqueda = this.filtroBusqueda().toLowerCase().trim();
    if (busqueda) {
      filtrados = filtrados.filter(
        (doc) =>
          doc.empleadoNombre?.toLowerCase().includes(busqueda) ||
          doc.tipoNombre?.toLowerCase().includes(busqueda) ||
          doc.id.toString().includes(busqueda),
      );
    }

    // Filtro por tipo
    const tipoId = this.filtroTipoId();
    if (tipoId) {
      filtrados = filtrados.filter((doc) => doc.tipoId === tipoId);
    }

    // Filtro por estado
    const estado = this.filtroEstado();
    if (estado === 'activos') {
      filtrados = filtrados.filter((doc) => doc.activo);
    } else if (estado === 'inactivos') {
      filtrados = filtrados.filter((doc) => !doc.activo);
    } else if (estado === 'vencidos') {
      filtrados = filtrados.filter((doc) => this.estaVencido(doc));
    } else if (estado === 'por-vencer') {
      const hoy = new Date();
      const treintaDias = new Date();
      treintaDias.setDate(treintaDias.getDate() + 30);
      filtrados = filtrados.filter(
        (doc) =>
          doc.fechaVencimiento &&
          new Date(doc.fechaVencimiento) >= hoy &&
          new Date(doc.fechaVencimiento) <= treintaDias,
      );
    }

    // Filtro por fecha desde
    if (this.filtroFechaDesde()) {
      const fechaDesde = new Date(this.filtroFechaDesde());
      filtrados = filtrados.filter(
        (doc) => doc.fechaCarga && new Date(doc.fechaCarga) >= fechaDesde,
      );
    }

    // Filtro por fecha hasta
    if (this.filtroFechaHasta()) {
      const fechaHasta = new Date(this.filtroFechaHasta());
      fechaHasta.setHours(23, 59, 59);
      filtrados = filtrados.filter(
        (doc) => doc.fechaCarga && new Date(doc.fechaCarga) <= fechaHasta,
      );
    }

    // Ordenamiento (incluyendo fechaEmision)
    const campo = this.ordenCampo();
    const direccion = this.ordenDireccion();

    filtrados.sort((a, b) => {
      let valorA = a[campo as keyof DocumentoPrivadoResponse] || '';
      let valorB = b[campo as keyof DocumentoPrivadoResponse] || '';

      // Manejar fechas
      if (campo === 'fechaCarga' || campo === 'fechaVencimiento' || campo === 'fechaEmision') {
        valorA = new Date(valorA as string).getTime() as any;
        valorB = new Date(valorB as string).getTime() as any;
      }

      if (valorA < valorB) return direccion === 'asc' ? -1 : 1;
      if (valorA > valorB) return direccion === 'asc' ? 1 : -1;
      return 0;
    });

    this.documentosFiltrados.set(filtrados);
  }

  /**
   * Verifica si un documento está vencido
   */
  estaVencido(doc: DocumentoPrivadoResponse): boolean {
    if (!doc.fechaVencimiento) return false;
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaVenc = new Date(doc.fechaVencimiento);
    return fechaVenc < hoy;
  }

  /**
   * Calcula días restantes para vencimiento
   */
  diasRestantes(doc: DocumentoPrivadoResponse): number | null {
    if (!doc.fechaVencimiento) return null;
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaVenc = new Date(doc.fechaVencimiento);
    const diferencia = fechaVenc.getTime() - hoy.getTime();
    return Math.ceil(diferencia / (1000 * 60 * 60 * 24));
  }

  /**
   * Obtiene la URL completa del documento
   */
  urlCompleta(doc: DocumentoPrivadoResponse): string {
    if (!doc.archivoUrl) return '';
    if (doc.archivoUrl.startsWith('http://') || doc.archivoUrl.startsWith('https://')) {
      return doc.archivoUrl;
    }
    return `${environment.apiUrl}${doc.archivoUrl}`;
  }

  /**
   * Ver el documento en nueva pestaña
   */
  verDocumento(doc: DocumentoPrivadoResponse): void {
    if (doc.archivoUrl) {
      window.open(this.urlCompleta(doc), '_blank');
    } else {
      alert('Archivo no disponible.');
    }
  }

  /**
   * Eliminar documento
   */
  eliminar(doc: DocumentoPrivadoResponse): void {
    if (!confirm(`¿Eliminar el documento de ${doc.empleadoNombre}?`)) return;

    this.documentoService
      .delete(doc.id)
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al eliminar documento:', error);
          alert('No se pudo eliminar el documento.');
          return of(null);
        }),
      )
      .subscribe({
        next: () => {
          this.cargarDocumentos();
        },
      });
  }

  /**
   * Limpia todos los filtros
   */
  limpiarFiltros(): void {
    this.filtroBusqueda.set('');
    this.filtroTipoId.set(null);
    this.filtroEstado.set('todos');
    this.filtroFechaDesde.set('');
    this.filtroFechaHasta.set('');
    this.aplicarFiltros();
  }

  /**
   * Cambia el ordenamiento
   */
  cambiarOrden(campo: string): void {
    if (this.ordenCampo() === campo) {
      this.ordenDireccion.set(this.ordenDireccion() === 'asc' ? 'desc' : 'asc');
    } else {
      this.ordenCampo.set(campo);
      this.ordenDireccion.set('asc');
    }
    this.aplicarFiltros();
  }

  /**
   * Obtiene el nombre del tipo por ID
   */
  getTipoNombre(tipoId: number): string {
    const tipo = this.tiposDocumento().find((t) => t.idTipo === tipoId);
    return tipo?.nombre || 'Sin tipo';
  }
}
