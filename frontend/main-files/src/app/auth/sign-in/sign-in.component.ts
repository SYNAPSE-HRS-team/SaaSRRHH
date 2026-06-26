import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css'],
})
export class SignInComponent implements OnInit {
  // ========== PROPIEDADES ==========
  email: string = '';
  password: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;
  showPassword: boolean = false;

  // ========== CONSTRUCTOR ==========
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  // ========== MÉTODOS DEL CICLO DE VIDA ==========
  ngOnInit(): void {
    // Si ya está autenticado, redirigir al dashboard
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard/default']);
    }
  }

  // ========== MÉTODOS DEL LOGIN ==========

  // Método para iniciar sesión
  onLogin(): void {
    if (!this.email || !this.password) {
      this.errorMessage = 'Por favor, complete todos los campos';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        console.log(' Login exitoso', response);
        this.isLoading = false;

        if (response.token) {
          localStorage.setItem('token', response.token);
        }

        if (response.email) {
          localStorage.setItem('email', response.email);
        }

        //  GUARDAR ROLES CON PREFIJO "ROLE_"
        if (response.roles) {
          const rolesConPrefijo = response.roles.map((rol: string) =>
            rol.startsWith('ROLE_') ? rol : `ROLE_${rol}`,
          );
          localStorage.setItem('roles', JSON.stringify(rolesConPrefijo));
          console.log(' Roles guardados (con prefijo):', rolesConPrefijo);
        }

        this.router.navigate(['/dashboard/default']);
      },
      error: (error) => {
        console.error('Error en login', error);
        this.isLoading = false;
        this.errorMessage =
          error.error?.error ||
          'Error al iniciar sesión. Verifique sus credenciales.';
      },
    });
  }

  // Método para ir a registro
  onSignup(): void {
    this.router.navigate(['/auth/sign-up']);
  }

  // Método para ir a recuperar contraseña
  onForgotpassword(): void {
    this.router.navigate(['/auth/forgot-password']);
  }

  // Método para mostrar/ocultar contraseña
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
    const passwordInput = document.getElementById(
      'inputChoosePassword',
    ) as HTMLInputElement;
    if (passwordInput) {
      passwordInput.type = this.showPassword ? 'text' : 'password';
    }
  }
}
