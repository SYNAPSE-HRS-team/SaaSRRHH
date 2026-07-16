import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Planilla, PlanillaRequest } from '../models/planilla.model';

@Injectable({ providedIn: 'root' })
export class PlanillaService {
  private readonly baseUrl = `${environment.apiUrl}/api/planillas`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Planilla[]> {
    return this.http.get<Planilla[]>(this.baseUrl);
  }

  buscarPorId(id: number): Observable<Planilla> {
    return this.http.get<Planilla>(`${this.baseUrl}/${id}`);
  }

  guardar(planilla: PlanillaRequest): Observable<Planilla> {
    return this.http.post<Planilla>(this.baseUrl, planilla);
  }

  actualizar(id: number, planilla: Partial<PlanillaRequest>): Observable<Planilla> {
    return this.http.put<Planilla>(`${this.baseUrl}/${id}`, planilla);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
