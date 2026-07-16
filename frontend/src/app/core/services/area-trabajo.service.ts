import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AreaTrabajo } from '../models/area-trabajo.model';
import { BaseService } from './base.service';

@Injectable({ providedIn: 'root' })
export class AreaTrabajoService extends BaseService<AreaTrabajo, AreaTrabajo> {
  constructor(http: HttpClient) {
    super(http, 'areas-trabajo');
  }

  listarActivas(): Observable<AreaTrabajo[]> {
    return this.http.get<AreaTrabajo[]>(`${this.baseUrl}/activas`);
  }

  buscarPorNombre(nombre: string): Observable<AreaTrabajo> {
    return this.http.get<AreaTrabajo>(`${this.baseUrl}/buscar`, {
      params: { nombre },
    });
  }
}
