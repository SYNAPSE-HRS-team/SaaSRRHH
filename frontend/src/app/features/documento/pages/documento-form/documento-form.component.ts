import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DocumentoPrivado } from '../../../../core/models/documento-privado.model';
import { DocumentoService } from '../../../../core/services/documento.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';

@Component({
  selector: 'app-documento-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './documento-form.component.html',
  styleUrls: ['./documento-form.component.scss']
})
export class DocumentoFormComponent implements OnInit {
  formData: DocumentoPrivado = {
    nombre: '',
    tipoDocumento: '',
    contenido: '',
    idEmpleado: 0,
    fechaSubida: ''
  };

  empleados: EmpleadoResponse[] = [];
  empleadosCargados = signal(false);
  saving = signal(false);
  error = signal('');
  successMessage = signal('');

  tiposDocumento = ['CONTRATO', 'BOLETA', 'DNI', 'CERTIFICADO', 'OTROS'];

  constructor(
    private documentoService: DocumentoService,
    private empleadoService: EmpleadoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.cargarEmpleados();
  }

  cargarEmpleados(): void {
    this.empleadoService.listarActivos().subscribe({
      next: (data) => {
        this.empleados = data;
        this.empleadosCargados.set(true);
      },
      error: (err) => {
        this.error.set('Error al cargar la lista de colaboradores');
        this.empleadosCargados.set(true);
      },
    });
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.formData.nombre = file.name;
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const resultString = reader.result as string;
        this.formData.contenido = resultString.includes(',') ? resultString.split(',')[1] : resultString;
      };
    }
  }

  obtenerTipoId(tipoTexto: string): number {
    switch (tipoTexto) {
      case 'CONTRATO': return 1;
      case 'BOLETA': return 2;
      case 'DNI': return 3;
      case 'CERTIFICADO': return 4;
      default: return 5;
    }
  }

  onSubmit(): void {
    const datos = this.formData as any;

    if (!datos.idEmpleado || datos.idEmpleado == 0) {
      this.error.set('Debes seleccionar un empleado');
      return;
    }

    this.saving.set(true);

    // Payload idéntico al DTO de Java: DocumentoPrivadoRequestDTO
    const payload: DocumentoPrivado = {
      nombre: datos.nombre,
      tipoDocumento: datos.tipoDocumento,
      contenido: datos.contenido,
      idEmpleado: Number(datos.idEmpleado),
      fechaSubida: new Date().toISOString().split('T')[0]
    };

    this.documentoService.create(payload).subscribe({
      next: () => {
        this.saving.set(false);
        this.successMessage.set('¡Documento indexado correctamente!');
        setTimeout(() => this.router.navigate(['/documentos']), 1500);
      },
      error: (err) => {
        this.saving.set(false);
        console.error('Error:', err);
        this.error.set('Error 400: Verifica que los IDs de los tipos de documento existan en tu BD.');
      },
    });
  }
}