import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MetricaBurnoutRequest, MetricaBurnoutResponse } from '../models/metrica-burnout.model';

@Injectable({
    providedIn: 'root'
})
export class MetricaBurnoutService {
    private apiUrl = '/api/burnout';  // ✅ Sin environment.apiUrl, usa el proxy

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

    obtenerAlertas(): Observable<MetricaBurnoutResponse[]> {
        return this.http.get<MetricaBurnoutResponse[]>(`${this.apiUrl}/alertas`);
    }

    obtenerResumen(): Observable<any> {
        return this.http.get(`${this.apiUrl}/resumen`);
    }

    obtenerPatronesDetectados(): Observable<MetricaBurnoutResponse[]> {
        return this.http.get<MetricaBurnoutResponse[]>(`${this.apiUrl}/patrones-detectados`);
    }

    recalcularParaEmpleado(empleadoId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/recalcular/${empleadoId}`, {});
    }
}