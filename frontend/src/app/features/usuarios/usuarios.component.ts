import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.scss']
})
export class UsuariosComponent implements OnInit {
  private usuarioService = inject(UsuarioService);
  private fb = inject(FormBuilder);

  usuarios: any[] = [];
  usuarioForm!: FormGroup;
  isModalOpen = false;
  editMode = false;

  ngOnInit(): void {
    this.cargarUsuarios();
    this.initForm();
  }

  initForm(): void {
    this.usuarioForm = this.fb.group({
      id: [null],
      email: ['', [Validators.required, Validators.email]],
      password: [''],
      nombre: [''],        // ← AGREGADO
      apellido: [''],      // ← AGREGADO
      telefono: [''],      // ← AGREGADO
      rolId: [3, Validators.required],
      activo: [true],
    });
  }

  cargarUsuarios(): void {
    this.usuarioService.listar().subscribe({
      next: (data) => {
        this.usuarios = data;
      },
      error: (err) => console.error('Error al cargar la lista de usuarios', err),
    });
  }

  abrirModal(): void {
    this.editMode = false;
    this.usuarioForm.reset({ rolId: 3, activo: true });
    this.usuarioForm.get('password')?.setValidators([Validators.required]);
    this.usuarioForm.get('password')?.updateValueAndValidity();
    this.isModalOpen = true;
  }

  editarUsuario(usuario: any): void {
    this.editMode = true;

    // Al editar la contraseña pasa a ser un campo opcional
    this.usuarioForm.get('password')?.clearValidators();
    this.usuarioForm.get('password')?.updateValueAndValidity();

    this.usuarioForm.patchValue({
      id: usuario.id,
      email: usuario.email,
      nombre: usuario.nombre || '',        // ← AGREGADO
      apellido: usuario.apellido || '',    // ← AGREGADO
      telefono: usuario.telefono || '',    // ← AGREGADO
      activo: usuario.activo ?? true,
      rolId: usuario.rol?.idRol || usuario.rol?.id || usuario.rolId || 3,
      password: '', // Inicializa vacío por seguridad
    });
    this.isModalOpen = true;
  }

  cerrarModal(): void {
    this.isModalOpen = false;
  }

  onSubmit(): void {
    if (this.usuarioForm.invalid) return;

    const formValue = this.usuarioForm.value;

    // ✅ Datos completos que espera el backend
    const data: any = {
      email: formValue.email,
      nombre: formValue.nombre || null,        // ← AGREGADO
      apellido: formValue.apellido || null,    // ← AGREGADO
      telefono: formValue.telefono || null,    // ← AGREGADO
      activo: formValue.activo,
      rolId: Number(formValue.rolId),
    };

    // Solo incluir password si tiene valor
    if (formValue.password) {
      data.password = formValue.password;
    }

    if (this.editMode) {
      const id = formValue.id;

      this.usuarioService.actualizar(id, data).subscribe({
        next: () => {
          this.cargarUsuarios();
          this.cerrarModal();
        },
        error: (err) => {
          console.error('Error al actualizar:', err);
          alert('Error al actualizar el usuario.');
        },
      });
    } else {
      // Para crear, la contraseña es obligatoria
      if (!data.password) {
        alert('La contraseña es obligatoria para nuevos usuarios');
        return;
      }

      this.usuarioService.guardar(data).subscribe({
        next: () => {
          this.cargarUsuarios();
          this.cerrarModal();
        },
        error: (err) => {
          console.error('Error al crear:', err);
          alert('Error al crear el usuario.');
        },
      });
    }
  }

  eliminarUsuario(id: number): void {
    if (!id) return;
    if (confirm('¿Seguro que deseas eliminar este usuario?')) {
      this.usuarioService.eliminar(id).subscribe({
        next: () => this.cargarUsuarios(),
        error: (err) => console.error(err),
      });
    }
  }
}