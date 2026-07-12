import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
    horaEntrada: '08:00',
    horaSalida: '17:00',
    diasLaborables: 'LUN,MAR,MIE,JUE,VIE',
    toleranciaMinutos: 10,
    tipoPago: 'MENSUAL',
    montoPago: undefined,
  };

  usuarios: any[] = [];
  usuariosCargados = signal(false);
  isEditMode = false;
  empleadoId?: number;
  saving = signal(false);
  error = signal('');
  successMessage = signal('');

  constructor(
    private empleadoService: EmpleadoService,
    private usuarioService: UsuarioService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const paramId = Number(this.route.snapshot.paramMap.get('id'));
    if (paramId && !Number.isNaN(paramId)) {
      this.isEditMode = true;
      this.empleadoId = paramId;
      this.cargarEmpleado(paramId);
    } else {
      this.cargarUsuarios();
    }
  }

  cargarUsuarios(currentUsuarioId?: number): void {
    this.usuarioService.listarSinEmpleado().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.usuariosCargados.set(true);
        if (currentUsuarioId) {
          this.usuarioService.obtener(currentUsuarioId).subscribe({
            next: (currentUser) => {
              if (!this.usuarios.some((u) => u.id === currentUser.id)) {
                this.usuarios.unshift(currentUser);
              }
            },
            error: (err) => console.error('Error cargando usuario actual:', err),
          });
        }
        if (data.length === 0 && !currentUsuarioId) {
          this.error.set('No hay usuarios disponibles. Crea un usuario primero.');
        }
      },
      error: (err) => {
        console.error('Error cargando usuarios:', err);
        this.error.set('Error al cargar la lista de usuarios');
        this.usuariosCargados.set(true);
      },
    });
  }

  cargarEmpleado(id: number): void {
    this.empleadoService.getById(id).subscribe({
      next: (empleado) => {
        this.formData = {
          usuarioId: empleado.usuarioId || 0,
          nombres: empleado.nombres || '',
          apellidos: empleado.apellidos || '',
          dni: empleado.dni || '',
          cargo: empleado.cargo || '',
          sueldoBase: empleado.sueldoBase,
          fechaInicioContrato: empleado.fechaInicioContrato || '',
          fechaFinContrato: empleado.fechaFinContrato || '',
          asignacionFamiliar: empleado.asignacionFamiliar || false,
          activo: empleado.activo ?? true,
          fotoPerfilUrl: empleado.fotoPerfilUrl || '',
          horaEntrada: empleado.horaEntrada || '08:00',
          horaSalida: empleado.horaSalida || '17:00',
          diasLaborables: empleado.diasLaborables || 'LUN,MAR,MIE,JUE,VIE',
          toleranciaMinutos: empleado.toleranciaMinutos ?? 10,
          tipoPago: empleado.tipoPago || 'MENSUAL',
          montoPago: empleado.montoPago,
        };
        this.cargarUsuarios(empleado.usuarioId || undefined);
      },
      error: (err) => {
        console.error('Error al cargar empleado:', err);
        this.error.set('No se pudo cargar el empleado para edición');
      },
    });
  }

  onUsuarioChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const id = Number(select.value);
    this.formData.usuarioId = id;

    if (!id || id === 0) {
      this.formData.nombres = '';
      this.formData.apellidos = '';
      return;
    }

    this.error.set('');
    this.usuarioService.obtener(id).subscribe({
      next: (usuario) => {
        this.formData.nombres = usuario.nombre || '';
        this.formData.apellidos = usuario.apellido || '';
      },
      error: (err) => {
        console.error('Error obteniendo datos del usuario:', err);
        this.error.set('No se pudo cargar los datos del usuario seleccionado');
        this.formData.nombres = '';
        this.formData.apellidos = '';
      },
    });
  }

  onSubmit(): void {
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

    const operation = this.isEditMode && this.empleadoId
      ? this.empleadoService.update(this.empleadoId, this.formData)
      : this.empleadoService.create(this.formData);

    operation.subscribe({
      next: (response) => {
        this.saving.set(false);
        this.successMessage.set(
          this.isEditMode
            ? `Empleado "${response.nombres} ${response.apellidos}" actualizado exitosamente`
            : `Empleado "${response.nombres} ${response.apellidos}" creado exitosamente`,
        );
        setTimeout(() => this.router.navigate(['/empleados']), 1500);
      },
      error: (err) => {
        this.saving.set(false);
        console.error('Error:', err);
        this.error.set(err.error?.message || (this.isEditMode ? 'Error al actualizar el empleado' : 'Error al crear el empleado'));
      },
    });
  }
}