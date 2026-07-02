import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { TipoDocumentoRequest, TipoDocumentoResponse } from '../models/tipo-documento.model';

@Injectable({ providedIn: 'root' })
export class TipoDocumentoService extends BaseService<TipoDocumentoRequest, TipoDocumentoResponse> {
  constructor(http: HttpClient) {
    super(http, 'tipos-documento');
  }
}