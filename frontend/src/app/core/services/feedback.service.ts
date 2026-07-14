import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  private apiUrl = '/api/feedback-anonimo';

  constructor(private http: HttpClient) {}

  enviarFeedback(data: FeedbackRequest): Observable<FeedbackResponse> {
    return this.http.post<FeedbackResponse>(this.apiUrl, data);
  }

  listar(): Observable<FeedbackResponse[]> {
    return this.http.get<FeedbackResponse[]>(this.apiUrl);
  }

  listarMisFeedbacks(empleadoId: number): Observable<FeedbackResponse[]> {
    return this.http.get<FeedbackResponse[]>(`${this.apiUrl}/mis-feedbacks`, { params: { empleadoId } });
  }

  responderFeedback(id: number, respuesta: string, estado: string): Observable<FeedbackResponse> {
    return this.http.post<FeedbackResponse>(`${this.apiUrl}/${id}/responder`, { respuesta, estado });
  }
}