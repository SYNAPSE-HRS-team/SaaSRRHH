import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FeedbackRequest {
  mensaje: string;
  categoria: string;
  empleadoId?: number;
  esAnonimo?: boolean;
}

export interface FeedbackResponse {
  id: number;
  mensaje: string;
  categoria: string;
  estado: string;
  fechaEnvio: string;
  empleadoId?: number;
  nombreEmpleado?: string;
  esAnonimo?: boolean;
  respuesta?: string;
  fechaRespuesta?: string;
}

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  // 1. Dejamos el endpoint base apuntando a la API general
  private baseApiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  enviarFeedback(data: FeedbackRequest): Observable<FeedbackResponse> {
    // 2. Elegimos dinámicamente la ruta según si es anónimo o no
    const url = data.esAnonimo
      ? `${this.baseApiUrl}/feedback-anonimo`
      : `${this.baseApiUrl}/feedback`;

    return this.http.post<FeedbackResponse>(url, data);
  }

  // Las funciones de listar y responder usualmente van al endpoint general/admin
  listar(): Observable<FeedbackResponse[]> {
    return this.http.get<FeedbackResponse[]>(`${this.baseApiUrl}/feedback-anonimo`);
  }

  listarMisFeedbacks(empleadoId: number): Observable<FeedbackResponse[]> {
    return this.http.get<FeedbackResponse[]>(`${this.baseApiUrl}/feedback-anonimo/mis-feedbacks`, {
      params: { empleadoId },
    });
  }

  responderFeedback(id: number, respuesta: string, estado: string): Observable<FeedbackResponse> {
    return this.http.post<FeedbackResponse>(`${this.baseApiUrl}/feedback-anonimo/${id}/responder`, {
      respuesta,
      estado,
    });
  }
}
