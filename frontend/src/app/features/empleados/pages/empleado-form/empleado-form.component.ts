import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EmpleadoRequest } from '../../../../core/models/empleado.model';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { UsuarioService } from '../../../../core/services/usuario.service';

@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './empleado-form.component.html',
  styleUrls: ['./empleado-form.component.scss']
})
export class EmpleadoFormComponent implements OnInit {
  formData: EmpleadoRequest = {
    usuarioId: 0,
    nombres: '',
    apellidos: '',
    dni: '',
    cargo: '',
    sueldoBase: undefined,
    fechaInicioContrato: '',
    fechaFinContrato: '',
    asignacionFamiliar: false,
    activo: true,
    fotoPerfilUrl: '',
  };

  usuarios: any[] = [];
  usuariosCargados = signal(false);

  saving = signal(false);
  error = signal('');
  successMessage = signal('');

  constructor(
    private empleadoService: EmpleadoService,
    private usuarioService: UsuarioService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  // ✅ Cargar usuarios sin empleado
  cargarUsuarios(): void {
    console.log('🔄 Cargando usuarios sin empleado...');

    this.usuarioService.listarSinEmpleado().subscribe({
      next: (data) => {
        console.log('✅ Usuarios disponibles (sin empleado):', data);
        this.usuarios = data;
        this.usuariosCargados.set(true);

        if (data.length === 0) {
          this.error.set('No hay usuarios disponibles. Crea un usuario primero.');
        }
      },
      error: (err) => {
        console.error('❌ Error cargando usuarios:', err);
        this.error.set('Error al cargar la lista de usuarios');
        this.usuariosCargados.set(true);
      },
    });
  }

  // ✅ Método corregido - Recibe el evento y extrae el valor con tipado seguro
  onUsuarioChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const id = Number(select.value);
    
    this.formData.usuarioId = id;

    // Si selecciona "Seleccionar usuario..." (valor 0)
    if (!id || id === 0) {
      this.formData.nombres = '';
      this.formData.apellidos = '';
      return;
    }

    this.error.set('');

    // ✅ Obtener datos del usuario seleccionado para autocompletar
    this.usuarioService.obtener(id).subscribe({
      next: (usuario) => {
        // ✅ Autocompletar nombres y apellidos
        this.formData.nombres = usuario.nombre || '';
        this.formData.apellidos = usuario.apellido || '';
        console.log('✅ Datos autocompletados:', {
          nombres: this.formData.nombres,
          apellidos: this.formData.apellidos
        });
      },
      error: (err) => {
        console.error('❌ Error obteniendo datos del usuario seleccionado:', err);
        this.error.set('No se pudo cargar los datos del usuario seleccionado');
        // Limpiar campos en caso de error
        this.formData.nombres = '';
        this.formData.apellidos = '';
      },
    });
  }

  onSubmit(): void {
    // Validaciones
    if (!this.formData.usuarioId || this.formData.usuarioId === 0) {
      this.error.set('Debes seleccionar un usuario');
      return;
    }

    if (!this.formData.nombres?.trim() || !this.formData.apellidos?.trim()) {
      this.error.set('Nombres y apellidos son obligatorios');
      return;
    }

    if (!this.formData.dni?.trim() || this.formData.dni.length !== 8) {
      this.error.set('El DNI debe tener exactamente 8 dígitos');
      return;
    }

    this.saving.set(true);
    this.error.set('');
    this.successMessage.set('');

    console.log('📤 Enviando empleado:', this.formData);

    this.empleadoService.create(this.formData).subscribe({
      next: (response) => {
        this.saving.set(false);
        this.successMessage.set(
          `Empleado "${response.nombres} ${response.apellidos}" creado exitosamente`,
        );
        setTimeout(() => this.router.navigate(['/empleados']), 1500);
      },
      error: (err) => {
        this.saving.set(false);
        console.error('❌ Error:', err);
        this.error.set(err.error?.message || 'Error al crear el empleado');
      },
    });
  }
}