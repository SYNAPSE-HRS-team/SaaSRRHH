import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    // Obtener el token del localStorage
    const token = this.authService.getToken();

    console.log('🚀 Interceptor ejecutándose para:', req.url);
    console.log('🔑 Token:', token ? 'SÍ existe' : 'NO existe');

    // Si hay token, clonar la petición y agregar el header Authorization
    if (token) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`),
      });
      console.log('✅ Headers agregados');
      return next.handle(authReq);
    }

    // Si no hay token, enviar la petición sin modificar
    console.log('❌ No hay token, petición sin auth');
    return next.handle(req);
  }
}
