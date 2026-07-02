import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { DocumentoPrivado } from '../models/documento-privado.model';

@Injectable({ providedIn: 'root' })
export class DocumentoService extends BaseService<DocumentoPrivado, DocumentoPrivado> {
  constructor(http: HttpClient) {
    // 🚀 CAMBIADO: De 'documentos' a 'documentos-privados' para que haga match con Spring Security
    super(http, 'documentos-privados');
  }
}