import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UsuariosService } from '../usuarios.service';

@Component({
  selector: 'app-usuario-form',
  templateUrl: './usuario-form.component.html',
  styleUrls: ['./usuario-form.component.scss'],
})
export class UsuarioFormComponent implements OnInit {
  // Objeto que almacena los datos del formulario
  usuario: any = {
    email: '',
    password: '',
    role: 'USER',
  };

  error: string = '';

  constructor(
    private service: UsuariosService,
    private router: Router,
  ) {}

  ngOnInit(): void {}

  // Método que se ejecuta al enviar el formulario
  guardar(): void {
    console.log('📝 Guardando usuario:', this.usuario);

    this.service.crear(this.usuario).subscribe({
      next: (response) => {
        console.log('✅ Usuario creado:', response);
        alert('Usuario creado correctamente');
        this.router.navigate(['/usuarios']);
      },
      error: (error) => {
        console.error('Error al crear:', error);
        this.error = 'Error al crear el usuario. Intenta de nuevo.';
      },
    });
  }
}
