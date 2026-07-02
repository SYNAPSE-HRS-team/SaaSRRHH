import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TipoDocumentoRequest, TipoDocumentoResponse } from '../../../../core/models/tipo-documento.model';
import { TipoDocumentoService } from '../../../../core/services/tipo-documento.service';

@Component({
  selector: 'app-tipo-documento-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './tipo-documento-list.component.html',
  styleUrls: ['./tipo-documento-list.component.scss']
})
export class TipoDocumentoListComponent implements OnInit {
  tipos: TipoDocumentoResponse[] = [];
  cargando = signal(true);
  errorHttp = signal(false);
  guardando = signal(false);
  error = signal('');

  mostrandoForm = false;
  editandoId: number | null = null;

  // campos del formulario
  nombre = '';
  obligatorio = false;
  requiereRenovacion = false;
  diasVigencia: number | null = null;
  descripcion = '';

  constructor(private tipoDocumentoService: TipoDocumentoService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.errorHttp.set(false);

    this.tipoDocumentoService.getAll().subscribe({
      next: (data) => {
        this.tipos = data;
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar tipos de documento:', err);
        this.errorHttp.set(true);
        this.cargando.set(false);
      }
    });
  }

  abrirNuevo(): void {
    this.editandoId = null;
    this.nombre = '';
    this.obligatorio = false;
    this.requiereRenovacion = false;
    this.diasVigencia = null;
    this.descripcion = '';
    this.error.set('');
    this.mostrandoForm = true;
  }

  editar(tipo: TipoDocumentoResponse): void {
    this.editandoId = tipo.idTipo;
    this.nombre = tipo.nombre;
    this.obligatorio = tipo.obligatorio;
    this.requiereRenovacion = tipo.requiereRenovacion;
    this.diasVigencia = tipo.diasVigencia ?? null;
    this.descripcion = tipo.descripcion ?? '';
    this.error.set('');
    this.mostrandoForm = true;
  }

  cancelar(): void {
    this.mostrandoForm = false;
    this.editandoId = null;
  }

  guardar(): void {
    this.error.set('');

    if (!this.nombre.trim()) {
      this.error.set('El nombre es obligatorio.');
      return;
    }

    this.guardando.set(true);

    const payload: TipoDocumentoRequest = {
      nombre: this.nombre.trim(),
      obligatorio: this.obligatorio,
      requiereRenovacion: this.requiereRenovacion,
      diasVigencia: this.requiereRenovacion ? this.diasVigencia : null,
      descripcion: this.descripcion.trim() || undefined,
    };

    const request$ = this.editandoId
      ? this.tipoDocumentoService.update(this.editandoId, payload)
      : this.tipoDocumentoService.create(payload);

    request$.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrandoForm = false;
        this.editandoId = null;
        this.cargar();
      },
      error: (err) => {
        console.error('Error al guardar tipo de documento:', err);
        this.guardando.set(false);
        this.error.set('No se pudo guardar. Verifica que el nombre no esté repetido.');
      }
    });
  }

  eliminar(tipo: TipoDocumentoResponse): void {
    if (!confirm(`¿Eliminar la categoría "${tipo.nombre}"?`)) return;

    this.tipoDocumentoService.delete(tipo.idTipo).subscribe({
      next: () => this.cargar(),
      error: (err) => {
        console.error('Error al eliminar tipo de documento:', err);
        alert('No se pudo eliminar. Puede que ya tenga documentos asociados.');
      }
    });
  }
}