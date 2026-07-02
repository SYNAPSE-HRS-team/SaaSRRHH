import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BoletaPago } from '../models/boleta-pago.model';

@Injectable({ providedIn: 'root' })
export class BoletaPagoService {
  private readonly baseUrl = '/api/boletas_pago';
  private readonly nominaUrl = '/api/nomina';

  constructor(private http: HttpClient) {}

  listar(): Observable<BoletaPago[]> {
    return this.http.get<BoletaPago[]>(this.baseUrl);
  }

  /** GET /api/boletas_pago/mis-boletas — solo las boletas del empleado autenticado */
  listarMisBoletas(): Observable<BoletaPago[]> {
    return this.http.get<BoletaPago[]>(`${this.baseUrl}/mis-boletas`);
  }

  buscarPorId(id: number): Observable<BoletaPago> {
    return this.http.get<BoletaPago>(`${this.baseUrl}/${id}`);
  }

  guardar(boleta: Partial<BoletaPago>): Observable<BoletaPago> {
    return this.http.post<BoletaPago>(this.baseUrl, boleta);
  }

  actualizar(id: number, boleta: Partial<BoletaPago>): Observable<BoletaPago> {
    return this.http.put<BoletaPago>(`${this.baseUrl}/${id}`, boleta);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * GET /api/nomina/boleta/{id}/pdf
   * Descarga el PDF de la boleta. Usar saveBlob() para abrir/descargar en el navegador.
   */
  descargarPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.nominaUrl}/boleta/${id}/pdf`, { responseType: 'blob' });
  }

  /** Helper para disparar la descarga del blob en el navegador */
  saveBlob(blob: Blob, filename: string): void {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }
}
