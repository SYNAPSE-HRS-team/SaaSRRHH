import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Planilla } from '../models/planilla.model';
import { BonoDescuento } from '../models/boleta-pago.model';

@Injectable({ providedIn: 'root' })
export class NominaService {
  private readonly baseUrl = '/api/nomina';

  constructor(private http: HttpClient) {}

  /**
   * POST /api/nomina/generar?mes=&anio=
   * Genera una planilla completa para el período indicado.
   */
  generarPlanilla(mes: number, anio: number): Observable<Planilla> {
    const params = new HttpParams()
      .set('mes', mes.toString())
      .set('anio', anio.toString());
    return this.http.post<Planilla>(`${this.baseUrl}/generar`, null, { params });
  }

  /**
   * GET /api/nomina/planillas
   * Retorna todas las planillas generadas.
   */
  listarPlanillas(): Observable<Planilla[]> {
    return this.http.get<Planilla[]>(`${this.baseUrl}/planillas`);
  }

  /**
   * POST /api/nomina/planillas/{id}/cerrar
   * Cierra una planilla (cambia estado a CERRADO y asigna fecha de cierre).
   */
  cerrarPlanilla(id: number): Observable<Planilla> {
    return this.http.post<Planilla>(`${this.baseUrl}/planillas/${id}/cerrar`, null);
  }

  /**
   * POST /api/nomina/bonos
   * Crea un bono o descuento para un empleado.
   */
  crearBono(bono: BonoDescuento): Observable<BonoDescuento> {
    return this.http.post<BonoDescuento>(`${this.baseUrl}/bonos`, bono);
  }

  /**
   * GET /api/nomina/boleta/{id}/pdf
   * Descarga el PDF de una boleta de pago.
   */
  descargarBoletaPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/boleta/${id}/pdf`, { responseType: 'blob' });
  }
}
