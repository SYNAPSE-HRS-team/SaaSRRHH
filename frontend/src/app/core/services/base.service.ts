import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export abstract class BaseService<T, R = T> {
  protected baseUrl: string;

  constructor(protected http: HttpClient, endpoint: string) {
    this.baseUrl = `/api/${endpoint}`;
  }

  getAll(): Observable<R[]> {
    return this.http.get<R[]>(this.baseUrl);
  }

  getById(id: number): Observable<R> {
    return this.http.get<R>(`${this.baseUrl}/${id}`);
  }

  create(data: T): Observable<R> {
    return this.http.post<R>(this.baseUrl, data);
  }

  update(id: number, data: T): Observable<R> {
    return this.http.put<R>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
