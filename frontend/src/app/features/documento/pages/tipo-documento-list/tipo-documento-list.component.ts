import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  TipoDocumentoRequest,
  TipoDocumentoResponse,
} from '../../../../core/models/tipo-documento.model';
import { TipoDocumentoService } from '../../../../core/services/tipo-documento.service';

@Component({
  selector: 'app-tipo-documento-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './tipo-documento-list.component.html',
  styleUrls: ['./tipo-documento-list.component.scss'],
})
export class TipoDocumentoListComponent implements OnInit {
  // Cambiar a signal para que sea reactivo
  tipos = signal<TipoDocumentoResponse[]>([]);

  // Signals para estado
  cargando = signal(true);
  errorHttp = signal(false);
  guardando = signal(false);
  error = signal('');

  // Filtros (como signals)
  filtroBusqueda = signal('');
  filtroObligatorio = signal('todos'); // 'todos' | 'si' | 'no'
  filtroRenovacion = signal('todos'); // 'todos' | 'si' | 'no'

  // Tipos filtrados (computado usando signals)
  tiposFiltrados = computed(() => {
    let filtrados = this.tipos(); // Ahora tipos es un signal

    // Filtro por búsqueda
    const busqueda = this.filtroBusqueda().toLowerCase().trim();
    if (busqueda) {
      filtrados = filtrados.filter(
        (tipo) =>
          tipo.nombre.toLowerCase().includes(busqueda) ||
          tipo.descripcion?.toLowerCase().includes(busqueda),
      );
    }

    // Filtro por obligatorio
    if (this.filtroObligatorio() === 'si') {
      filtrados = filtrados.filter((tipo) => tipo.obligatorio);
    } else if (this.filtroObligatorio() === 'no') {
      filtrados = filtrados.filter((tipo) => !tipo.obligatorio);
    }

    // Filtro por renovación
    if (this.filtroRenovacion() === 'si') {
      filtrados = filtrados.filter((tipo) => tipo.requiereRenovacion);
    } else if (this.filtroRenovacion() === 'no') {
      filtrados = filtrados.filter((tipo) => !tipo.requiereRenovacion);
    }

    return filtrados;
  });

  // Estado del formulario
  mostrandoForm = false;
  editandoId: number | null = null;

  // Campos del formulario
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
        this.tipos.set(data); // Actualizar el signal
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar tipos de documento:', err);
        this.errorHttp.set(true);
        this.cargando.set(false);
      },
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
      },
    });
  }

  eliminar(tipo: TipoDocumentoResponse): void {
    if (!confirm(`¿Eliminar la categoría "${tipo.nombre}"?`)) return;

    this.tipoDocumentoService.delete(tipo.idTipo).subscribe({
      next: () => this.cargar(),
      error: (err) => {
        console.error('Error al eliminar tipo de documento:', err);
        alert('No se pudo eliminar. Puede que ya tenga documentos asociados.');
      },
    });
  }

  limpiarFiltros(): void {
    this.filtroBusqueda.set('');
    this.filtroObligatorio.set('todos');
    this.filtroRenovacion.set('todos');
  }
}
