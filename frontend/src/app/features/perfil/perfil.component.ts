import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Subject, takeUntil, finalize } from 'rxjs';
import { UsuarioService } from '../../core/services/usuario.service';
import { AuthService } from '../../core/services/auth.service';
import { EmpleadoService } from '../../core/services/empleado.service';
import { UsuarioResponse } from '../../core/models/usuario.model';
import { UsuarioRequest } from '../../core/models/usuario.model';
import { EmpleadoResponse } from '../../core/models/empleado.model';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss'],
  standalone: true,                    // ← AGREGAR ESTO
  imports: [ReactiveFormsModule]       // ← AGREGAR ESTO
})

export class PerfilComponent implements OnInit, OnDestroy {
  user: UsuarioResponse | null = null;
  empleado: EmpleadoResponse | null = null;
  profileForm!: FormGroup;
  isEditing = false;
  isLoading = true;
  isSaving = false;
  successMessage = '';
  errorMessage = '';

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private empleadoService: EmpleadoService,
    private authService: AuthService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.cargarPerfilCompleto();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.profileForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      apellido: ['', [Validators.required, Validators.minLength(2)]],
      email: [{ value: '', disabled: true }],
      telefono: ['', [Validators.pattern(/^[0-9]{9,15}$/)]],
      rol: [{ value: '', disabled: true }],
      cargo: [{ value: '', disabled: true }],
      fechaIngreso: [{ value: '', disabled: true }],
      dni: [{ value: '', disabled: true }],
      sueldoBase: [{ value: '', disabled: true }]
    });
  }

  cargarPerfilCompleto(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.usuarioService.getProfile()
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (userData: UsuarioResponse) => {
          this.user = userData;
          this.actualizarDatosUsuario(userData);

          if (userData.id) {
            this.cargarEmpleado(userData.id);
          } else {
            this.isLoading = false;
          }
        },
        error: (error: any) => {
          console.error('Error al cargar perfil:', error);
          this.errorMessage = 'Error al cargar la información del perfil';
          this.isLoading = false;
        }
      });
  }

  private cargarEmpleado(userId: number): void {
    this.empleadoService.buscarPorUsuarioId(userId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isLoading = false)
      )
      .subscribe({
        next: (empleadoData: EmpleadoResponse) => {
          this.empleado = empleadoData;
          this.actualizarDatosEmpleado(empleadoData);
        },
        error: (error: any) => {
          console.log('No se encontró empleado asociado:', error);
          this.isLoading = false;
        }
      });
  }

  private actualizarDatosUsuario(userData: UsuarioResponse): void {
    this.profileForm.patchValue({
      nombre: userData.nombre || '',
      apellido: userData.apellido || '',
      email: userData.email || '',
      telefono: userData.telefono || '',
      rol: userData.rol?.nombreRol || userData.rol?.nombre || 'Sin rol'
    });
  }

  private actualizarDatosEmpleado(empleadoData: EmpleadoResponse): void {
    this.profileForm.patchValue({
      cargo: empleadoData.cargo || 'No asignado',
      fechaIngreso: this.formatearFechaSimple(empleadoData.fechaInicioContrato),
      dni: empleadoData.dni || 'No disponible',
      sueldoBase: empleadoData.sueldoBase ?
        `S/. ${empleadoData.sueldoBase.toFixed(2)}` : 'No disponible'
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    this.successMessage = '';
    this.errorMessage = '';

    if (!this.isEditing && this.user) {
      this.actualizarDatosUsuario(this.user);
      if (this.empleado) {
        this.actualizarDatosEmpleado(this.empleado);
      }
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.errorMessage = 'Por favor, corrija los errores en el formulario';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formData = {
      nombre: this.profileForm.get('nombre')?.value,
      apellido: this.profileForm.get('apellido')?.value,
      telefono: this.profileForm.get('telefono')?.value
    };

    console.log('📤 Enviando:', JSON.stringify(formData, null, 2));

    if (!this.user?.id) {
      this.errorMessage = 'Error al identificar el usuario';
      this.isSaving = false;
      return;
    }

    // ✅ Usar el nuevo endpoint
    this.usuarioService.actualizarPerfil(this.user.id, formData)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isSaving = false)
      )
      .subscribe({
        next: (updatedUser: UsuarioResponse) => {
          console.log('✅ Actualizado:', updatedUser);
          this.user = updatedUser;
          this.actualizarDatosUsuario(updatedUser);
          this.successMessage = 'Perfil actualizado exitosamente';
          this.isEditing = false;
        },
        error: (error: any) => {
          console.error('❌ Error:', error);
          this.errorMessage = error.error?.error || 'Error al actualizar el perfil';
        }
      });
  }

  getInitials(): string {
    if (!this.user) return '?';

    const nombre = this.user.nombre || this.user.email?.split('@')[0] || '';
    const apellido = this.user.apellido || '';

    const inicialNombre = nombre.charAt(0).toUpperCase();
    const inicialApellido = apellido ? apellido.charAt(0).toUpperCase() : '';

    return (inicialNombre + inicialApellido) || '?';
  }

  getNombreCompleto(): string {
    if (!this.user) return 'Usuario';
    const nombre = this.user.nombre || '';
    const apellido = this.user.apellido || '';

    if (nombre || apellido) {
      return `${nombre} ${apellido}`.trim();
    }

    return this.user.email?.split('@')[0] || 'Usuario';
  }

  get estadoUsuario(): 'Activo' | 'Inactivo' {
    return this.user?.activo ? 'Activo' : 'Inactivo';
  }

  get ultimoAcceso(): string {
    return this.user?.ultimoAcceso ?
      this.formatearFecha(this.user.ultimoAcceso) :
      'Primera vez';
  }

  get fechaIngreso(): string {
    if (!this.empleado?.fechaInicioContrato) return '';
    return this.formatearFechaSimple(this.empleado.fechaInicioContrato);
  }

  hasError(controlName: string, errorType: string): boolean {
    const control = this.profileForm.get(controlName);
    return !!(control && control.hasError(errorType) && (control.dirty || control.touched));
  }

  getErrorMessage(controlName: string): string {
    const control = this.profileForm.get(controlName);

    if (!control || !control.errors) return '';

    if (control.errors['required']) return 'Este campo es obligatorio';
    if (control.errors['minlength']) return `Mínimo ${control.errors['minlength'].requiredLength} caracteres`;
    if (control.errors['pattern']) return 'Formato de teléfono inválido (9-15 dígitos)';

    return 'Campo inválido';
  }

  private formatearFecha(fecha: string | Date): string {
    if (!fecha) return '';

    const date = new Date(fecha);
    return date.toLocaleDateString('es-PE', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  private formatearFechaSimple(fecha?: string | Date): string {
    if (!fecha) return 'No especificada';

    const date = new Date(fecha);
    return date.toLocaleDateString('es-PE', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}