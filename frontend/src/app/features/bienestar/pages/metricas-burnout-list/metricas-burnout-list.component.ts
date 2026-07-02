import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MetricaBurnoutResponse } from '../../../../core/models/metrica-burnout.model';
import { MetricaBurnoutService } from '../../../../core/services/metrica-burnout.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';

@Component({
    selector: 'app-metricas-burnout-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './metricas-burnout-list.component.html',
    styleUrls: ['./metricas-burnout-list.component.scss']
})
export class MetricasBurnoutListComponent implements OnInit {
    metricas: MetricaBurnoutResponse[] = [];
    metricasFiltradas: MetricaBurnoutResponse[] = [];
    loading = false;
    error = '';

    // Filtros
    filtros = {
        nivelRiesgo: '',
        empleadoId: null as number | null,
    };

    // Lista de empleados para el filtro
    empleados: EmpleadoResponse[] = [];
    opcionesRiesgo = ['BAJO', 'MEDIO', 'ALTO'];

    constructor(
        private metricaService: MetricaBurnoutService,
        private empleadoService: EmpleadoService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.cargarEmpleados();
        this.cargarMetricas();
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

    cargarMetricas(): void {
        this.loading = true;
        this.metricaService.listar().subscribe({
            next: (data) => {
                // Enriquecer con nombre del empleado
                this.metricas = data.map(m => ({
                    ...m,
                    nombreEmpleado: this.getNombreEmpleado(m.empleadoId)
                }));
                this.aplicarFiltros();
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Error al cargar las métricas de burnout';
                this.loading = false;
                console.error(err);
            }
        });
    }

    getNombreEmpleado(empleadoId: number): string {
        const empleado = this.empleados.find(e => e.id === empleadoId);
        return empleado ? `${empleado.nombres} ${empleado.apellidos}` : `ID: ${empleadoId}`;
    }

    aplicarFiltros(): void {
        let resultado = [...this.metricas];

        if (this.filtros.nivelRiesgo) {
            resultado = resultado.filter(m => m.nivelRiesgo === this.filtros.nivelRiesgo);
        }

        if (this.filtros.empleadoId) {
            resultado = resultado.filter(m => m.empleadoId === this.filtros.empleadoId);
        }

        this.metricasFiltradas = resultado;
    }

    limpiarFiltros(): void {
        this.filtros = {
            nivelRiesgo: '',
            empleadoId: null,
        };
        this.aplicarFiltros();
    }

    nuevaMetrica(): void {
        this.router.navigate(['/bienestar/metricas/nuevo']);
    }

    verDetalle(id: number): void {
        this.router.navigate(['/bienestar/metricas', id]);
    }

    editar(id: number): void {
        this.router.navigate(['/bienestar/metricas/editar', id]);
    }

    eliminar(id: number): void {
        if (confirm('¿Estás seguro de eliminar esta métrica?')) {
            this.metricaService.eliminar(id).subscribe({
                next: () => {
                    this.cargarMetricas();
                },
                error: (err) => {
                    alert('Error al eliminar');
                    console.error(err);
                }
            });
        }
    }

    getColorRiesgo(nivel: string): string {
        const colores = {
            'BAJO': '#22c55e',   // Verde
            'MEDIO': '#f59e0b',  // Amarillo
            'ALTO': '#ef4444'    // Rojo
        };
        return colores[nivel as keyof typeof colores] || '#64748b';
    }

    getIconoRiesgo(nivel: string): string {
        const iconos = {
            'BAJO': '🟢',
            'MEDIO': '🟡',
            'ALTO': '🔴'
        };
        return iconos[nivel as keyof typeof iconos] || '📊';
    }

    getTextoRiesgo(nivel: string): string {
        const textos = {
            'BAJO': 'Bajo riesgo - Bienestar saludable',
            'MEDIO': 'Riesgo moderado - Monitorear',
            'ALTO': 'Alto riesgo - ¡Intervenir!'
        };
        return textos[nivel as keyof typeof textos] || nivel;
    }

    // Estadísticas
    get totalMetricas(): number {
        return this.metricasFiltradas.length;
    }

    get metricasAltas(): number {
        return this.metricasFiltradas.filter(m => m.nivelRiesgo === 'ALTO').length;
    }

    get metricasMedias(): number {
        return this.metricasFiltradas.filter(m => m.nivelRiesgo === 'MEDIO').length;
    }

    get metricasBajas(): number {
        return this.metricasFiltradas.filter(m => m.nivelRiesgo === 'BAJO').length;
    }
}