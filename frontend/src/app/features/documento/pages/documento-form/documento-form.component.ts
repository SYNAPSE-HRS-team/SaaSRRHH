import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize, Subject, takeUntil } from 'rxjs';
import { DocumentoPrivadoRequest } from '../../../../core/models/documento-privado.model';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';
import {
  TipoDocumentoRequest,
  TipoDocumentoResponse,
} from '../../../../core/models/tipo-documento.model';
import { DocumentoService } from '../../../../core/services/documento.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { TipoDocumentoService } from '../../../../core/services/tipo-documento.service';

const OPCION_NUEVO_TIPO = -1;

@Component({
  selector: 'app-documento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, MatIconModule],
  templateUrl: './documento-form.component.html',
  styleUrls: ['./documento-form.component.scss'],
})
export class DocumentoFormComponent implements OnInit, OnDestroy {
  // Formulario reactivo
  documentoForm!: FormGroup;

  // Listas de catálogos
  empleados: EmpleadoResponse[] = [];
  tipos: TipoDocumentoResponse[] = [];

  // Estado para crear nuevo tipo
  readonly OPCION_NUEVO_TIPO = OPCION_NUEVO_TIPO;
  mostrandoNuevoTipo = false;
  nuevoTipoNombre = '';
  nuevoTipoDias = 365;
  creandoTipo = signal(false);
  esEdicion = false;
  documentoId?: number;

  // Estados de carga
  cargandoCatalogos = signal(true);
  cargandoDocumento = signal(false);
  subiendoArchivo = signal(false);
  guardando = signal(false);
  error = signal('');
  successMessage = signal('');

  // Archivo seleccionado
  archivoSeleccionado: File | null = null;
  nombreArchivo = '';
  archivoUrl = '';
  archivoUrlOriginal = '';

  // Manejo de destrucción
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private documentoService: DocumentoService,
    private tipoDocumentoService: TipoDocumentoService,
    private empleadoService: EmpleadoService,
    private route: ActivatedRoute,
    public router: Router,
  ) {}

  ngOnInit(): void {
    this.initializeForm();

    // Verificar si es edición
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe((params) => {
      if (params['id']) {
        this.esEdicion = true;
        this.documentoId = +params['id'];
        console.log('✏️ Modo edición - ID:', this.documentoId);
        this.cargarCatalogos(() => {
          this.cargarDocumento(this.documentoId!);
        });
      } else {
        this.esEdicion = false;
        this.cargarCatalogos();
        this.configurarEscuchasFormulario();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Inicializa el formulario reactivo
   */
  private initializeForm(): void {
    const today = new Date().toISOString().split('T')[0];

    this.documentoForm = this.fb.group({
      empleadoId: [null, Validators.required],
      tipoId: [null, Validators.required],
      fechaEmision: [today, Validators.required],
      fechaVencimiento: [{ value: '', disabled: true }],
      archivoUrl: ['', Validators.required],
      activo: [true],
    });
  }

  /**
   * Configura los listeners del formulario
   */
  private configurarEscuchasFormulario(): void {
    this.documentoForm
      .get('tipoId')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((tipoId) => {
        if (tipoId === OPCION_NUEVO_TIPO) {
          this.mostrandoNuevoTipo = true;
          this.documentoForm.patchValue({ tipoId: null });
        } else {
          this.mostrandoNuevoTipo = false;
          this.calcularFechaVencimiento();
        }
      });

    this.documentoForm
      .get('fechaEmision')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.calcularFechaVencimiento();
      });
  }

  /**
   * Calcula la fecha de vencimiento
   */
  private calcularFechaVencimiento(): void {
    const tipoId = this.documentoForm.get('tipoId')?.value;
    const fechaEmision = this.documentoForm.get('fechaEmision')?.value;

    if (!tipoId || !fechaEmision) {
      this.documentoForm.patchValue({ fechaVencimiento: '' });
      return;
    }

    const tipoSeleccionado = this.tipos.find((tipo) => tipo.idTipo === tipoId);
    if (!tipoSeleccionado || !tipoSeleccionado.diasVigencia) {
      this.documentoForm.patchValue({ fechaVencimiento: '' });
      return;
    }

    const fechaEmisionDate = new Date(fechaEmision);
    fechaEmisionDate.setDate(fechaEmisionDate.getDate() + tipoSeleccionado.diasVigencia);

    const year = fechaEmisionDate.getFullYear();
    const month = String(fechaEmisionDate.getMonth() + 1).padStart(2, '0');
    const day = String(fechaEmisionDate.getDate()).padStart(2, '0');
    const fechaVencimiento = `${year}-${month}-${day}`;

    this.documentoForm.patchValue({ fechaVencimiento });
  }

  /**
   * Carga los catálogos
   */
  private cargarCatalogos(callback?: () => void): void {
    this.cargandoCatalogos.set(true);

    this.empleadoService
      .listarActivos()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.empleados = data;
        },
        error: (err) => console.error('Error cargando empleados:', err),
      });

    this.tipoDocumentoService
      .getAll()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.cargandoCatalogos.set(false);
          if (callback) callback();
        }),
      )
      .subscribe({
        next: (data) => {
          this.tipos = data;
          if (!this.esEdicion && this.tipos.length > 0) {
            this.documentoForm.patchValue({ tipoId: this.tipos[0].idTipo });
            setTimeout(() => this.calcularFechaVencimiento(), 100);
          }
        },
        error: (err) => console.error('Error cargando tipos:', err),
      });
  }

  /**
   * Carga el documento para edición
   */
  private cargarDocumento(id: number): void {
    this.cargandoDocumento.set(true);
    this.documentoService
      .getById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          if (!data) return;

          this.documentoForm.patchValue({
            empleadoId: data.empleadoId,
            tipoId: data.tipoId,
            fechaEmision: data.fechaEmision ? this.formatDate(data.fechaEmision) : '',
            fechaVencimiento: data.fechaVencimiento ? this.formatDate(data.fechaVencimiento) : '',
            archivoUrl: data.archivoUrl || '',
            activo: data.activo !== undefined ? data.activo : true,
          });

          this.archivoUrl = data.archivoUrl || '';
          this.archivoUrlOriginal = data.archivoUrl || '';

          // Deshabilitar campos en edición
          this.documentoForm.get('empleadoId')?.disable();
          this.documentoForm.get('tipoId')?.disable();
          this.documentoForm.get('activo')?.disable();

          this.configurarEscuchasFormulario();
          this.cargandoDocumento.set(false);
        },
        error: (error) => {
          console.error('Error cargando documento:', error);
          this.error.set('No se pudo cargar el documento');
          this.cargandoDocumento.set(false);
        },
      });
  }

  private formatDate(dateString: string): string {
    if (!dateString) return '';
    try {
      const date = new Date(dateString);
      return date.toISOString().split('T')[0];
    } catch {
      return '';
    }
  }

  onTipoChange(): void {
    if (this.esEdicion) return;
    const tipoId = this.documentoForm.get('tipoId')?.value;
    this.mostrandoNuevoTipo = tipoId === OPCION_NUEVO_TIPO;
    if (this.mostrandoNuevoTipo) {
      this.documentoForm.patchValue({ tipoId: null });
    }
  }

  cancelarNuevoTipo(): void {
    if (this.esEdicion) return;
    this.mostrandoNuevoTipo = false;
    this.nuevoTipoNombre = '';
    this.nuevoTipoDias = 365;
    if (this.tipos.length > 0) {
      this.documentoForm.patchValue({ tipoId: this.tipos[0].idTipo });
      this.calcularFechaVencimiento();
    }
  }

  crearNuevoTipo(): void {
    if (this.esEdicion) return;
    this.error.set('');

    if (!this.nuevoTipoNombre.trim()) {
      this.error.set('Escribe el nombre del nuevo tipo de documento.');
      return;
    }

    if (!this.nuevoTipoDias || this.nuevoTipoDias < 1) {
      this.error.set('Los días de vigencia deben ser mayor a 0.');
      return;
    }

    this.creandoTipo.set(true);
    const payload: TipoDocumentoRequest = {
      nombre: this.nuevoTipoNombre.trim(),
      obligatorio: false,
      requiereRenovacion: false,
      diasVigencia: this.nuevoTipoDias,
    };

    this.tipoDocumentoService
      .create(payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (tipoCreado) => {
          if (tipoCreado) {
            this.creandoTipo.set(false);
            this.tipos.push(tipoCreado);
            this.documentoForm.patchValue({ tipoId: tipoCreado.idTipo });
            this.mostrandoNuevoTipo = false;
            this.nuevoTipoNombre = '';
            this.nuevoTipoDias = 365;
            this.error.set('');
            this.calcularFechaVencimiento();
          }
        },
        error: (error) => {
          console.error('Error al crear tipo:', error);
          this.error.set('No se pudo crear el tipo de documento.');
          this.creandoTipo.set(false);
        },
      });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.archivoSeleccionado = file;
      this.nombreArchivo = file.name;
      this.subirArchivo();
    }
  }

  private subirArchivo(): void {
    if (!this.archivoSeleccionado) return;
    this.subiendoArchivo.set(true);
    this.error.set('');

    this.documentoService
      .subirArchivo(this.archivoSeleccionado)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.subiendoArchivo.set(false);
          if (response && response.url) {
            this.archivoUrl = response.url;
            this.documentoForm.patchValue({ archivoUrl: response.url });
            this.error.set('');
          }
        },
        error: (error) => {
          console.error('Error al subir archivo:', error);
          this.error.set('No se pudo subir el archivo.');
          this.subiendoArchivo.set(false);
        },
      });
  }

  removeFile(): void {
    this.archivoSeleccionado = null;
    this.nombreArchivo = '';
    this.archivoUrl = this.esEdicion ? this.archivoUrlOriginal : '';
    this.documentoForm.patchValue({ archivoUrl: this.archivoUrl });
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  onSubmit(): void {
    this.error.set('');
    const formValues = this.documentoForm.getRawValue();

    if (this.documentoForm.invalid) {
      Object.keys(this.documentoForm.controls).forEach((key) => {
        this.documentoForm.get(key)?.markAsTouched();
      });
      this.error.set('Completa todos los campos obligatorios.');
      return;
    }

    if (!formValues.archivoUrl) {
      this.error.set('Debes subir un archivo.');
      return;
    }

    this.guardando.set(true);
    const payload: DocumentoPrivadoRequest = {
      empleadoId: Number(formValues.empleadoId),
      tipoId: Number(formValues.tipoId),
      archivoUrl: formValues.archivoUrl,
      fechaEmision: formValues.fechaEmision,
      fechaVencimiento: formValues.fechaVencimiento || null,
      activo: formValues.activo !== undefined ? formValues.activo : true,
    };

    const operation =
      this.esEdicion && this.documentoId
        ? this.documentoService.update(this.documentoId, payload)
        : this.documentoService.create(payload);

    operation.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        this.guardando.set(false);
        if (response) {
          this.successMessage.set(
            this.esEdicion ? '¡Documento actualizado!' : '¡Documento registrado!',
          );
          setTimeout(() => this.router.navigate(['/documentos']), 1500);
        }
      },
      error: (error) => {
        console.error('Error al guardar:', error);
        this.error.set('Error al guardar el documento.');
        this.guardando.set(false);
      },
    });
  }

  getDiasVigencia(): number | null {
    const tipoId = this.documentoForm.get('tipoId')?.value;
    if (!tipoId || tipoId === OPCION_NUEVO_TIPO) return null;
    const tipo = this.tipos.find((t) => t.idTipo === tipoId);
    return tipo?.diasVigencia || null;
  }

  getTipoNombre(): string {
    const tipoId = this.documentoForm.get('tipoId')?.value;
    if (!tipoId || tipoId === OPCION_NUEVO_TIPO) return '';
    const tipo = this.tipos.find((t) => t.idTipo === tipoId);
    return tipo?.nombre || '';
  }

  getEmpleadoNombre(): string {
    const empleadoId = this.documentoForm.get('empleadoId')?.value;
    if (!empleadoId) return '';
    const empleado = this.empleados.find((e) => e.id === empleadoId);
    return empleado ? `${empleado.nombres} ${empleado.apellidos}` : '';
  }

  isVencido(): boolean {
    const fechaVencimiento = this.documentoForm.get('fechaVencimiento')?.value;
    if (!fechaVencimiento) return false;
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaVenc = new Date(fechaVencimiento);
    return fechaVenc < hoy;
  }

  goToDocumentList(): void {
    this.router.navigate(['/documentos']);
  }
}

// ✅ UN SOLO EXPORT - ESTE ES EL COMPONENTE PRINCIPAL
// No agregues exports adicionales aquí
