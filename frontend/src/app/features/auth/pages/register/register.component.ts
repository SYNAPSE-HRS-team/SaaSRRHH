import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  userData = { email: '', password: '' };
  confirmarPassword = '';
  loading = signal(false);
  error = signal('');

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (!this.userData.email || !this.userData.password) {
      this.error.set('Todos los campos son obligatorios');
      return;
    }
    if (this.userData.password !== this.confirmarPassword) {
      this.error.set('Las contraseñas no coinciden');
      return;
    }
    if (this.userData.password.length < 6) {
      this.error.set('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.authService.register({ ...this.userData, rolId: 3 }).subscribe({
      next: () => {
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Error al crear la cuenta');
      }
    });
  }
}