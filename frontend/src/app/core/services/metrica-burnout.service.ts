import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MetricaBurnoutRequest, MetricaBurnoutResponse } from '../models/metrica-burnout.model';

@Injectable({
    providedIn: 'root'
})
export class MetricaBurnoutService {
    private apiUrl = `${environment.apiUrl}/api/burnout`;

    constructor(private http: HttpClient) {}

    listar(): Observable<MetricaBurnoutResponse[]> {
        return this.http.get<MetricaBurnoutResponse[]>(this.apiUrl);
    }

    obtenerPorId(id: number): Observable<MetricaBurnoutResponse> {
        return this.http.get<MetricaBurnoutResponse>(`${this.apiUrl}/${id}`);
    }

    buscarPorEmpleado(empleadoId: number): Observable<MetricaBurnoutResponse[]> {
        return this.http.get<MetricaBurnoutResponse[]>(`${this.apiUrl}/empleado/${empleadoId}`);
    }

    crear(dto: MetricaBurnoutRequest): Observable<MetricaBurnoutResponse> {
        return this.http.post<MetricaBurnoutResponse>(this.apiUrl, dto);
    }

    actualizar(id: number, dto: MetricaBurnoutRequest): Observable<MetricaBurnoutResponse> {
        return this.http.put<MetricaBurnoutResponse>(`${this.apiUrl}/${id}`, dto);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}