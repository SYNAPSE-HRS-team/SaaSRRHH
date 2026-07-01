import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DocumentoPrivadoRequest } from '../../../../core/models/documento-privado.model';
import { TipoDocumentoResponse } from '../../../../core/models/tipo-documento.model';
import { DocumentoService } from '../../../../core/services/documento.service';
import { TipoDocumentoService } from '../../../../core/services/tipo-documento.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';

@Component({
  selector: 'app-documento-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './documento-form.component.html',
  styleUrls: ['./documento-form.component.scss']
})
export class DocumentoFormComponent implements OnInit {
  empleadoId: number | null = null;
  tipoId: number | null = null;
  fechaVencimiento: string = '';

  archivoSeleccionado: File | null = null;
  nombreArchivo = '';

  empleados: EmpleadoResponse[] = [];
  tipos: TipoDocumentoResponse[] = [];

  cargandoCatalogos = signal(true);
  subiendoArchivo = signal(false);
  guardando = signal(false);
  error = signal('');
  successMessage = signal('');

  constructor(
    private documentoService: DocumentoService,
    private tipoDocumentoService: TipoDocumentoService,
    private empleadoService: EmpleadoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  private cargarCatalogos(): void {
    this.cargandoCatalogos.set(true);

    this.empleadoService.listarActivos().subscribe({
      next: (data) => (this.empleados = data),
      error: () => this.error.set('No se pudo cargar la lista de empleados.'),
    });

    this.tipoDocumentoService.getAll().subscribe({
      next: (data) => {
        this.tipos = data;
        this.cargandoCatalogos.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la lista de tipos de documento.');
        this.cargandoCatalogos.set(false);
      },
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.archivoSeleccionado = file;
      this.nombreArchivo = file.name;
    }
  }

  onSubmit(): void {
    this.error.set('');

    if (!this.empleadoId) {
      this.error.set('Debes seleccionar un empleado.');
      return;
    }
    if (!this.tipoId) {
      this.error.set('Debes seleccionar un tipo de documento.');
      return;
    }
    if (!this.archivoSeleccionado) {
      this.error.set('Debes seleccionar un archivo.');
      return;
    }

    this.subiendoArchivo.set(true);

    this.documentoService.subirArchivo(this.archivoSeleccionado).subscribe({
      next: ({ url }) => {
        this.subiendoArchivo.set(false);
        this.guardarRegistro(url);
      },
      error: (err) => {
        console.error('Error al subir archivo:', err);
        this.subiendoArchivo.set(false);
        this.error.set('No se pudo subir el archivo. Intenta nuevamente.');
      },
    });
  }

  private guardarRegistro(archivoUrl: string): void {
    this.guardando.set(true);

    const payload: DocumentoPrivadoRequest = {
      empleadoId: Number(this.empleadoId),
      tipoId: Number(this.tipoId),
      archivoUrl,
      fechaVencimiento: this.fechaVencimiento || null,
      activo: true,
    };

    this.documentoService.create(payload).subscribe({
      next: () => {
        this.guardando.set(false);
        this.successMessage.set('¡Documento registrado correctamente!');
        setTimeout(() => this.router.navigate(['/documentos']), 1200);
      },
      error: (err) => {
        console.error('Error al guardar documento:', err);
        this.guardando.set(false);
        this.error.set('Error al guardar el documento. Verifica los datos ingresados.');
      },
    });
  }
}