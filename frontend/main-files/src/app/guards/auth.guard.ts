// src/app/guards/auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(): boolean {
    console.log('AuthGuard ejecutándose...'); // ← Agrega este log
    const isAuth = this.authService.isAuthenticated();
    console.log('¿Está autenticado?', isAuth); // ← Agrega este log

    if (isAuth) {
      return true;
    }

    console.log('No autenticado, redirigiendo a login');
    this.router.navigate(['/auth/sign-in']);
    return false;
  }
}
