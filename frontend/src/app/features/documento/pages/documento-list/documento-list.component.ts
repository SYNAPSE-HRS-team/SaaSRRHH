import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DocumentoService } from '../../../../core/services/documento.service';
import { DocumentoPrivado } from '../../../../core/models/documento-privado.model';

@Component({
  selector: 'app-documento-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './documento-list.component.html',
  styleUrls: ['./documento-list.component.scss']
})
export class DocumentoListComponent implements OnInit {
  documentos: DocumentoPrivado[] = [];
  cargando: boolean = true;
  errorHttp: boolean = false;

  constructor(private documentoService: DocumentoService) { }

  ngOnInit(): void {
    this.cargarDocumentos();
  }

  cargarDocumentos(): void {
    this.cargando = true;
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

  verDocumento(doc: DocumentoPrivado): void {
    if (doc.contenido) {
      const win = window.open();
      win?.document.write(`<iframe src="${doc.contenido}" frameborder="0" style="border:0; top:0px; left:0px; bottom:0px; right:0px; width:100%; height:100%;" allowfullscreen></iframe>`);
    } else {
      alert('Contenido no disponible.');
    }
  }
}