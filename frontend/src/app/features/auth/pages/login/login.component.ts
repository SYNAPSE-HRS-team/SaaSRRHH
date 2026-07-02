import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginRequest } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  credentials: LoginRequest = { email: '', password: '' };
  loading = signal(false);
  error = signal('');
  showPassword = signal(false);

  constructor(private authService: AuthService, private router: Router) {}

  togglePasswordVisibility(): void {
    this.showPassword.update(current => !current);
  }

  onSubmit(): void {
    if (!this.credentials.email || !this.credentials.password) {
      this.error.set('Todos los campos son obligatorios');
      return;
    }

    // Expresión regular robusta para validar correos básicos y corporativos
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    
    if (!emailRegex.test(this.credentials.email)) {
      this.error.set('Por favor, ingresa un correo electrónico válido (ej. usuario@dominio.com)');
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