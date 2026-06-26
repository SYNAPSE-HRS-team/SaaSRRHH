import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginRequest } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <form (ngSubmit)="onSubmit()" class="login-form">
      <div class="form-group">
        <label for="email">Correo Electrónico</label>
        <input
          type="email"
          id="email"
          name="email"
          [(ngModel)]="credentials.email"
          placeholder="tu@correo.com"
          required
          autocomplete="email"
        />
      </div>
      <div class="form-group">
        <label for="password">Contraseña</label>
        <input
          type="password"
          id="password"
          name="password"
          [(ngModel)]="credentials.password"
          placeholder="Ingresa tu contraseña"
          required
          autocomplete="current-password"
        />
      </div>
      @if (error()) {
        <div class="error-message">{{ error() }}</div>
      }
      <button type="submit" class="btn-primary" [disabled]="loading()">
        @if (loading()) {
          <span class="spinner"></span>
          Iniciando sesión...
        } @else {
          Iniciar Sesión
        }
      </button>
      <p class="register-link">
        ¿No tienes cuenta? <a routerLink="/auth/register">Regístrate aquí</a>
      </p>
    </form>
  `,
  styles: [`
    .login-form {
      display: flex;
      flex-direction: column;
      gap: 1.25rem;
    }
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.4rem;
    }
    .form-group label {
      font-size: 0.85rem;
      font-weight: 600;
      color: #333;
    }
    .form-group input {
      padding: 0.75rem 1rem;
      border: 2px solid #e0e0e0;
      border-radius: 10px;
      font-size: 0.95rem;
      transition: border-color 0.2s;
      outline: none;
    }
    .form-group input:focus {
      border-color: #667eea;
    }
    .btn-primary {
      padding: 0.85rem;
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      border: none;
      border-radius: 10px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: transform 0.2s, box-shadow 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
    }
    .btn-primary:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: 0 4px 15px rgba(102,126,234,0.4);
    }
    .btn-primary:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
    .error-message {
      background: #fff0f0;
      color: #e74c3c;
      padding: 0.75rem;
      border-radius: 8px;
      font-size: 0.9rem;
      text-align: center;
    }
    .register-link {
      text-align: center;
      color: #666;
      font-size: 0.9rem;
    }
    .register-link a {
      color: #667eea;
      text-decoration: none;
      font-weight: 600;
    }
    .register-link a:hover {
      text-decoration: underline;
    }
    .spinner {
      width: 16px;
      height: 16px;
      border: 2px solid rgba(255,255,255,0.3);
      border-top-color: white;
      border-radius: 50%;
      animation: spin 0.6s linear infinite;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `]
})
export class LoginComponent {
  credentials: LoginRequest = { email: '', password: '' };
  loading = signal(false);
  error = signal('');

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (!this.credentials.email || !this.credentials.password) {
      this.error.set('Todos los campos son obligatorios');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.authService.login(this.credentials).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.error || 'Credenciales inválidas');
      }
    });
  }
}
