import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { catchError, finalize, of, Subject, takeUntil } from 'rxjs';
import { DocumentoPrivadoRequest } from '../../../../core/models/documento-privado.model';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';
import { TipoDocumentoRequest, TipoDocumentoResponse } from '../../../../core/models/tipo-documento.model';
import { DocumentoService } from '../../../../core/services/documento.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { TipoDocumentoService } from '../../../../core/services/tipo-documento.service';

const OPCION_NUEVO_TIPO = -1;

@Component({
  selector: 'app-documento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink], // <-- AÑADIR FormsModule
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
  creandoTipo = signal(false);

  // Estados de carga
  cargandoCatalogos = signal(true);
  subiendoArchivo = signal(false);
  guardando = signal(false);
  error = signal('');
  successMessage = signal('');

  // Archivo seleccionado
  archivoSeleccionado: File | null = null;
  nombreArchivo = '';
  archivoUrl = '';

  // Manejo de destrucción
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private documentoService: DocumentoService,
    private tipoDocumentoService: TipoDocumentoService,
    private empleadoService: EmpleadoService,
    public router: Router, // <-- CAMBIADO a public para usarlo en el template
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.cargarCatalogos();
    this.configurarEscuchasFormulario();
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
    // Cuando cambia el tipoId, recalcular fecha de vencimiento
    this.documentoForm
      .get('tipoId')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((tipoId) => {
        console.log('Tipo seleccionado:', tipoId);
        if (tipoId === OPCION_NUEVO_TIPO) {
          this.mostrandoNuevoTipo = true;
          this.documentoForm.patchValue({
            tipoId: null,
          });
        } else {
          this.mostrandoNuevoTipo = false;
          this.calcularFechaVencimiento();
        }
      });

    // Cuando cambia la fecha de emisión, recalcular fecha de vencimiento
    this.documentoForm
      .get('fechaEmision')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((fecha) => {
        console.log('Fecha de emisión:', fecha);
        this.calcularFechaVencimiento();
      });
  }

  /**
   * Calcula la fecha de vencimiento basada en el tipo y fecha de emisión
   */
  private calcularFechaVencimiento(): void {
    const tipoId = this.documentoForm.get('tipoId')?.value;
    const fechaEmision = this.documentoForm.get('fechaEmision')?.value;

    console.log('Calculando fecha de vencimiento:', { tipoId, fechaEmision });

    if (!tipoId || !fechaEmision) {
      this.documentoForm.patchValue({
        fechaVencimiento: '',
      });
      return;
    }

    const tipoSeleccionado = this.tipos.find((tipo) => tipo.idTipo === tipoId);

    console.log('Tipo seleccionado:', tipoSeleccionado);

    if (!tipoSeleccionado || !tipoSeleccionado.diasVigencia) {
      this.documentoForm.patchValue({
        fechaVencimiento: '',
      });
      return;
    }

    // Calcular fecha de vencimiento: fechaEmision + diasVigencia
    const fechaEmisionDate = new Date(fechaEmision);
    const diasVigencia = tipoSeleccionado.diasVigencia;

    fechaEmisionDate.setDate(fechaEmisionDate.getDate() + diasVigencia);

    const year = fechaEmisionDate.getFullYear();
    const month = String(fechaEmisionDate.getMonth() + 1).padStart(2, '0');
    const day = String(fechaEmisionDate.getDate()).padStart(2, '0');
    const fechaVencimiento = `${year}-${month}-${day}`;

    console.log('Fecha de vencimiento calculada:', fechaVencimiento);

    this.documentoForm.patchValue({
      fechaVencimiento: fechaVencimiento,
    });
  }

  /**
   * Carga los catálogos necesarios
   */
  private cargarCatalogos(): void {
    this.cargandoCatalogos.set(true);

    this.empleadoService
      .listarActivos()
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al cargar empleados:', error);
          this.error.set('No se pudo cargar la lista de empleados.');
          return of([]);
        }),
      )
      .subscribe({
        next: (data) => {
          this.empleados = data;
        },
      });

    this.tipoDocumentoService
      .getAll()
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al cargar tipos:', error);
          this.error.set('No se pudo cargar la lista de tipos de documento.');
          return of([]);
        }),
        finalize(() => this.cargandoCatalogos.set(false)),
      )
      .subscribe({
        next: (data) => {
          this.tipos = data;
          console.log('Tipos cargados:', this.tipos);
          if (this.tipos.length > 0) {
            this.documentoForm.patchValue({
              tipoId: this.tipos[0].idTipo,
            });
            setTimeout(() => this.calcularFechaVencimiento(), 100);
          }
        },
      });
  }

  /**
   * Maneja el cambio en el select de tipo
   */
  onTipoChange(): void {
    const tipoId = this.documentoForm.get('tipoId')?.value;
    this.mostrandoNuevoTipo = tipoId === OPCION_NUEVO_TIPO;

    if (this.mostrandoNuevoTipo) {
      this.documentoForm.patchValue({
        tipoId: null,
      });
    }
  }

  /**
   * Cancela la creación de un nuevo tipo
   */
  cancelarNuevoTipo(): void {
    this.mostrandoNuevoTipo = false;
    this.nuevoTipoNombre = '';
    if (this.tipos.length > 0) {
      this.documentoForm.patchValue({
        tipoId: this.tipos[0].idTipo,
      });
      this.calcularFechaVencimiento();
    }
  }

  /**
   * Crea un nuevo tipo de documento
   */
  crearNuevoTipo(): void {
    this.error.set('');

    if (!this.nuevoTipoNombre.trim()) {
      this.error.set('Escribe el nombre del nuevo tipo de documento.');
      return;
    }

    this.creandoTipo.set(true);

    const payload: TipoDocumentoRequest = {
      nombre: this.nuevoTipoNombre.trim(),
      obligatorio: false,
      requiereRenovacion: false,
      diasVigencia: 365,
    };

    this.tipoDocumentoService
      .create(payload)
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al crear tipo:', error);
          this.error.set('No se pudo crear el tipo de documento.');
          this.creandoTipo.set(false);
          return of(null);
        }),
      )
      .subscribe({
        next: (tipoCreado) => {
          if (tipoCreado) {
            this.creandoTipo.set(false);
            this.tipos.push(tipoCreado);
            this.documentoForm.patchValue({
              tipoId: tipoCreado.idTipo,
            });
            this.mostrandoNuevoTipo = false;
            this.nuevoTipoNombre = '';
            this.error.set('');
            this.calcularFechaVencimiento();
          }
        },
      });
  }

  /**
   * Maneja la selección de archivo
   */
  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.archivoSeleccionado = file;
      this.nombreArchivo = file.name;
      this.subirArchivo();
    }
  }

  /**
   * Sube el archivo al servidor
   */
  private subirArchivo(): void {
    if (!this.archivoSeleccionado) return;

    this.subiendoArchivo.set(true);
    this.error.set('');

    this.documentoService
      .subirArchivo(this.archivoSeleccionado)
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al subir archivo:', error);
          this.error.set('No se pudo subir el archivo. Intenta nuevamente.');
          this.subiendoArchivo.set(false);
          return of(null);
        }),
      )
      .subscribe({
        next: (response) => {
          this.subiendoArchivo.set(false);
          if (response && response.url) {
            this.archivoUrl = response.url;
            this.documentoForm.patchValue({
              archivoUrl: response.url,
            });
            this.error.set('');
          }
        },
      });
  }

  /**
   * Elimina el archivo seleccionado
   */
  removeFile(): void {
    this.archivoSeleccionado = null;
    this.nombreArchivo = '';
    this.archivoUrl = '';
    this.documentoForm.patchValue({
      archivoUrl: '',
    });
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  /**
   * Envía el formulario
   */
  onSubmit(): void {
    this.error.set('');

    if (this.documentoForm.invalid) {
      Object.keys(this.documentoForm.controls).forEach((key) => {
        const control = this.documentoForm.get(key);
        control?.markAsTouched();
      });

      if (!this.documentoForm.get('archivoUrl')?.value) {
        this.error.set('Debes seleccionar un archivo.');
      } else {
        this.error.set('Por favor, completa todos los campos obligatorios.');
      }
      return;
    }

    if (!this.documentoForm.get('archivoUrl')?.value) {
      this.error.set('El archivo no se ha subido correctamente.');
      return;
    }

    this.guardando.set(true);

    // Obtener la fecha de vencimiento calculada
    const fechaVencimiento = this.documentoForm.get('fechaVencimiento')?.value;

    // Crear el DTO con los datos del formulario
    const payload: DocumentoPrivadoRequest = {
      empleadoId: Number(this.documentoForm.get('empleadoId')?.value),
      tipoId: Number(this.documentoForm.get('tipoId')?.value),
      archivoUrl: this.documentoForm.get('archivoUrl')?.value,
      fechaEmision: this.documentoForm.get('fechaEmision')?.value, // <-- AGREGAR fechaEmision
      fechaVencimiento: fechaVencimiento || null,
      activo: this.documentoForm.get('activo')?.value,
    };

    console.log('Payload a enviar:', payload);

    this.documentoService
      .create(payload)
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          console.error('Error al guardar documento:', error);
          this.error.set('Error al guardar el documento. Verifica los datos ingresados.');
          this.guardando.set(false);
          return of(null);
        }),
      )
      .subscribe({
        next: (response) => {
          this.guardando.set(false);
          if (response) {
            this.successMessage.set('¡Documento registrado correctamente!');
            setTimeout(() => this.router.navigate(['/documentos']), 1500);
          }
        },
      });
  }

  /**
   * Obtiene los días de vigencia del tipo seleccionado
   */
  getDiasVigencia(): number | null {
    const tipoId = this.documentoForm.get('tipoId')?.value;
    if (!tipoId || tipoId === OPCION_NUEVO_TIPO) return null;
    const tipo = this.tipos.find((t) => t.idTipo === tipoId);
    return tipo?.diasVigencia || null;
  }

  /**
   * Obtiene el nombre del tipo seleccionado
   */
  getTipoNombre(): string {
    const tipoId = this.documentoForm.get('tipoId')?.value;
    if (!tipoId || tipoId === OPCION_NUEVO_TIPO) return '';
    const tipo = this.tipos.find((t) => t.idTipo === tipoId);
    return tipo?.nombre || '';
  }

  /**
   * Obtiene el nombre del empleado seleccionado
   */
  getEmpleadoNombre(): string {
    const empleadoId = this.documentoForm.get('empleadoId')?.value;
    if (!empleadoId) return '';
    const empleado = this.empleados.find((e) => e.id === empleadoId);
    return empleado ? `${empleado.nombres} ${empleado.apellidos}` : '';
  }

  /**
   * Verifica si la fecha de vencimiento ya pasó
   */
  isVencido(): boolean {
    const fechaVencimiento = this.documentoForm.get('fechaVencimiento')?.value;
    if (!fechaVencimiento) return false;
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaVenc = new Date(fechaVencimiento);
    return fechaVenc < hoy;
  }

  /**
   * Navega a la lista de documentos
   */
  goToDocumentList(): void {
    this.router.navigate(['/documentos']);
  }
}
