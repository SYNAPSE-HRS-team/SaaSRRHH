import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DocumentoService } from '../../../../core/services/documento.service';
import { DocumentoPrivadoResponse } from '../../../../core/models/documento-privado.model';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-documento-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './documento-list.component.html',
  styleUrls: ['./documento-list.component.scss']
})
export class DocumentoListComponent implements OnInit {
  documentos: DocumentoPrivadoResponse[] = [];
  cargando = true;
  errorHttp = false;

  constructor(private documentoService: DocumentoService) {}

  ngOnInit(): void {
    this.cargarDocumentos();
  }

  cargarDocumentos(): void {
    this.cargando = true;
    this.errorHttp = false;

    this.documentoService.getAll().subscribe({
      next: (data) => {
        this.documentos = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al conectar con la API de documentos:', err);
        this.errorHttp = true;
        this.cargando = false;
      }
    });
  }

  urlCompleta(doc: DocumentoPrivadoResponse): string {
  // Si ya viene como URL absoluta (http/https), se usa tal cual.
  // Si es una ruta relativa (/uploads/documentos/xxx.pdf), se le antepone el backend.
  if (!doc.archivoUrl) return '';
  if (doc.archivoUrl.startsWith('http://') || doc.archivoUrl.startsWith('https://')) {
    return doc.archivoUrl;
  }
  return `${environment.apiUrl}${doc.archivoUrl}`;
}

  verDocumento(doc: DocumentoPrivadoResponse): void {
    if (doc.archivoUrl) {
      window.open(this.urlCompleta(doc), '_blank');
    } else {
      alert('Archivo no disponible.');
    }
  }

  eliminar(doc: DocumentoPrivadoResponse): void {
    if (!confirm(`¿Eliminar el documento de ${doc.empleadoNombre}?`)) return;

    this.documentoService.delete(doc.id).subscribe({
      next: () => this.cargarDocumentos(),
      error: (err) => {
        console.error('Error al eliminar documento:', err);
        alert('No se pudo eliminar el documento.');
      }
    });
  }

  estaVencido(doc: DocumentoPrivadoResponse): boolean {
    if (!doc.fechaVencimiento) return false;
    return new Date(doc.fechaVencimiento) < new Date();
  }
}