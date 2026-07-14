import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EncuestaBienestarService } from '../../../../core/services/encuesta-bienestar.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-encuesta-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, MatIconModule],
    templateUrl: './encuesta-form.component.html',
    styleUrls: ['./encuesta-form.component.scss']
})
export class EncuestaFormComponent implements OnInit {
    form!: FormGroup;
    esEdicion = false;
    idEncuesta?: number;
    loading = false;
    submitted = false;
    error = '';
    success = '';

    empleados: EmpleadoResponse[] = [];

    constructor(
        private fb: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private encuestaService: EncuestaBienestarService,
        private empleadoService: EmpleadoService
    ) {}

    ngOnInit(): void {
        this.inicializarFormulario();
        this.cargarEmpleados();
        this.verificarParametros();
    }

    inicializarFormulario(): void {
        this.form = this.fb.group({
            empleadoId: ['', [Validators.required]],
            fecha: ['', [Validators.required]],
            cargaLaboral: ['', [Validators.required, Validators.min(1), Validators.max(5)]],
            apoyoEquipo: ['', [Validators.required, Validators.min(1), Validators.max(5)]],
            proyeccion: ['', [Validators.required, Validators.min(1), Validators.max(5)]]
        });
    }

    cargarEmpleados(): void {
        this.empleadoService.listarActivos().subscribe({
            next: (data) => {
                this.empleados = data;
            },
            error: (err) => {
                console.error('Error cargando empleados:', err);
            }
        });
    }

    verificarParametros(): void {
        this.route.params.subscribe((params) => {
            const id = params['id'];
            if (id) {
                this.esEdicion = true;
                this.idEncuesta = +id;
                this.cargarEncuesta(this.idEncuesta);
            }
        });
    }

    cargarEncuesta(id: number): void {
        this.loading = true;
        this.encuestaService.obtenerPorId(id).subscribe({
            next: (data) => {
                this.form.patchValue({
                    empleadoId: data.empleadoId,
                    fecha: data.fecha,
                    cargaLaboral: data.cargaLaboral,
                    apoyoEquipo: data.apoyoEquipo,
                    proyeccion: data.proyeccion
                });
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Error al cargar la encuesta';
                this.loading = false;
                console.error(err);
            }
        });
    }

    onSubmit(): void {
        this.submitted = true;
        this.error = '';
        this.success = '';

        if (this.form.invalid) {
            return;
        }

        this.loading = true;
        const dto = this.form.value;

        if (this.esEdicion && this.idEncuesta) {
            this.encuestaService.actualizar(this.idEncuesta, dto).subscribe({
                next: () => {
                    this.success = 'Encuesta actualizada exitosamente';
                    this.loading = false;
                    setTimeout(() => this.router.navigate(['/bienestar']), 1500);
                },
                error: (err) => {
                    this.error = err.error || 'Error al actualizar';
                    this.loading = false;
                }
            });
        } else {
            this.encuestaService.crear(dto).subscribe({
                next: () => {
                    this.success = 'Encuesta creada exitosamente';
                    this.loading = false;
                    setTimeout(() => this.router.navigate(['/bienestar']), 1500);
                },
                error: (err) => {
                    this.error = err.error || 'Error al crear';
                    this.loading = false;
                }
            });
        }
    }

    cancelar(): void {
        this.router.navigate(['/bienestar']);
    }

    getColor(valor: number): string {
        if (!valor) return '#9e9e9e';
        if (valor <= 2) return '#4caf50';
        if (valor <= 3) return '#ff9800';
        if (valor <= 4) return '#f44336';
        return '#9e9e9e';
    }

    getTexto(valor: number): string {
        if (!valor) return '—';
        if (valor <= 2) return 'Bajo';
        if (valor <= 3) return 'Medio';
        if (valor <= 4) return 'Alto';
        return 'Muy Alto';
    }

    getPromedio(): number {
        const carga = this.form.get('cargaLaboral')?.value || 0;
        const apoyo = this.form.get('apoyoEquipo')?.value || 0;
        const proy = this.form.get('proyeccion')?.value || 0;
        if (!carga && !apoyo && !proy) return 0;
        return Math.round((carga + apoyo + proy) / 3);
    }
}