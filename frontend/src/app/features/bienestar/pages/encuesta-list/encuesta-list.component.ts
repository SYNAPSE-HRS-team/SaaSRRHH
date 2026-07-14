import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EncuestaBienestarResponse } from '../../../../core/models/encuesta-bienestar.model';
import { EncuestaBienestarService } from '../../../../core/services/encuesta-bienestar.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-encuesta-list',
    standalone: true,
    imports: [CommonModule, FormsModule, MatIconModule],
    templateUrl: './encuesta-list.component.html',
    styleUrls: ['./encuesta-list.component.scss']
})
export class EncuestaListComponent implements OnInit {
    encuestasOriginales: EncuestaBienestarResponse[] = [];
    encuestasFiltradas: EncuestaBienestarResponse[] = [];
    loading = false;
    error = '';

    // ✅ FILTROS
    filtros = {
        empleadoId: null as number | null,
        fechaInicio: '',
        fechaFin: '',
        nivelBienestar: '',
    };

    // ✅ LISTA DE EMPLEADOS PARA EL FILTRO
    empleados: EmpleadoResponse[] = [];

    // ✅ OPCIONES DE NIVEL DE BIENESTAR
    opcionesNivel = ['BAJO', 'MEDIO', 'ALTO', 'CRITICO'];

    constructor(
        private encuestaService: EncuestaBienestarService,
        private empleadoService: EmpleadoService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.cargarEmpleados();
        this.cargarEncuestas();
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

    cargarEncuestas(): void {
        this.loading = true;
        this.encuestaService.listar().subscribe({
            next: (data) => {
                this.encuestasOriginales = data;
                this.aplicarFiltros();
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Error al cargar las encuestas';
                this.loading = false;
                console.error(err);
            }
        });
    }

    // ============================================
    // FILTROS
    // ============================================
    aplicarFiltros(): void {
        let resultado = [...this.encuestasOriginales];

        // Filtro por empleado
        if (this.filtros.empleadoId) {
            resultado = resultado.filter(e => e.empleadoId === this.filtros.empleadoId);
        }

        // Filtro por fecha inicio
        if (this.filtros.fechaInicio) {
            resultado = resultado.filter(e => e.fecha >= this.filtros.fechaInicio);
        }

        // Filtro por fecha fin
        if (this.filtros.fechaFin) {
            resultado = resultado.filter(e => e.fecha <= this.filtros.fechaFin);
        }

        // Filtro por nivel de bienestar
        if (this.filtros.nivelBienestar) {
            resultado = resultado.filter(e => e.nivelBienestar === this.filtros.nivelBienestar);
        }

        this.encuestasFiltradas = resultado;
    }

    limpiarFiltros(): void {
        this.filtros = {
            empleadoId: null,
            fechaInicio: '',
            fechaFin: '',
            nivelBienestar: '',
        };
        this.aplicarFiltros();
    }

    // ============================================
    // ACCIONES
    // ============================================
    nuevaEncuesta(): void {
        this.router.navigate(['/bienestar/encuestas/nuevo']);
    }

    verDetalle(id: number): void {
        this.router.navigate(['/bienestar/encuestas', id]);
    }

    editar(id: number): void {
        this.router.navigate(['/bienestar/encuestas/editar', id]);
    }

    eliminar(id: number): void {
        if (confirm('¿Estás seguro de eliminar esta encuesta?')) {
            this.encuestaService.eliminar(id).subscribe({
                next: () => {
                    this.cargarEncuestas();
                },
                error: (err) => {
                    alert('Error al eliminar');
                    console.error(err);
                }
            });
        }
    }

    // ============================================
    // UTILIDADES
    // ============================================
    getNombreEmpleado(encuesta: EncuestaBienestarResponse): string {
        return encuesta.nombreEmpleado || `ID: ${encuesta.empleadoId}`;
    }

    getColorNivel(nivel: string): string {
        const colores: { [key: string]: string } = {
            'BAJO': '#22c55e',      // Verde
            'MEDIO': '#f59e0b',     // Amarillo
            'ALTO': '#f97316',      // Naranja
            'CRITICO': '#ef4444',   // Rojo
        };
        return colores[nivel] || '#64748b';
    }

    getColorPromedio(promedio: number): string {
        if (promedio <= 2) return '#22c55e';
        if (promedio <= 3) return '#f59e0b';
        if (promedio <= 4) return '#f97316';
        return '#ef4444';
    }

    getIconoNivel(nivel: string): string {
        const iconos: { [key: string]: string } = {
            'BAJO': '😊',
            'MEDIO': '🙂',
            'ALTO': '😟',
            'CRITICO': '😰',
        };
        return iconos[nivel] || '📊';
    }

    getMaterialIconoNivel(nivel: string): string {
        const iconos: { [key: string]: string } = {
            'BAJO': 'sentiment_very_satisfied',
            'MEDIO': 'sentiment_satisfied',
            'ALTO': 'sentiment_dissatisfied',
            'CRITICO': 'sentiment_very_dissatisfied',
        };
        return iconos[nivel] || 'analytics';
    }

    // ============================================
    // CONTADORES PARA ESTADÍSTICAS
    // ============================================
    get totalEncuestas(): number {
        return this.encuestasFiltradas.length;
    }

    get promedioGeneral(): number {
        if (this.encuestasFiltradas.length === 0) return 0;
        const sum = this.encuestasFiltradas.reduce((acc, e) => acc + (e.promedioGeneral || 0), 0);
        return Math.round(sum / this.encuestasFiltradas.length);
    }

    get encuestasCriticas(): number {
        return this.encuestasFiltradas.filter(e => e.nivelBienestar === 'CRITICO' || e.nivelBienestar === 'ALTO').length;
    }

    get encuestasBuenas(): number {
        return this.encuestasFiltradas.filter(e => e.nivelBienestar === 'BAJO' || e.nivelBienestar === 'MEDIO').length;
    }
}