import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MetricaBurnoutResponse } from '../../../../core/models/metrica-burnout.model';
import { MetricaBurnoutService } from '../../../../core/services/metrica-burnout.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-metricas-burnout-list',
    standalone: true,
    imports: [CommonModule, FormsModule, MatIconModule],
    templateUrl: './metricas-burnout-list.component.html',
    styleUrls: ['./metricas-burnout-list.component.scss']
})
export class MetricasBurnoutListComponent implements OnInit {
    metricas: MetricaBurnoutResponse[] = [];
    metricasFiltradas: MetricaBurnoutResponse[] = [];
    loading = false;
    error = '';
    filtros = { nivelRiesgo: '', empleadoId: null as number | null };
    empleados: EmpleadoResponse[] = [];
    opcionesRiesgo = ['BAJO', 'MEDIO', 'ALTO'];

    constructor(private metricaService: MetricaBurnoutService, private empleadoService: EmpleadoService, private router: Router) {}

    ngOnInit(): void { this.cargarEmpleados(); this.cargarMetricas(); }

    cargarEmpleados(): void {
        this.empleadoService.listarActivos().subscribe({ next: (data) => { this.empleados = data; }, error: (err) => { console.error('Error cargando empleados:', err); } });
    }

    cargarMetricas(): void {
        this.loading = true;
        this.metricaService.listar().subscribe({
            next: (data) => { this.metricas = data; this.aplicarFiltros(); this.loading = false; },
            error: (err) => { this.error = 'Error al cargar las métricas de burnout'; this.loading = false; console.error(err); }
        });
    }

    recalcularTodas(): void {
        this.loading = true; this.error = '';
        this.metricaService.listar().subscribe({ next: (data) => { this.metricas = data; this.aplicarFiltros(); this.loading = false; }, error: (err) => { this.error = 'Error al recalcular'; this.loading = false; } });
    }

    aplicarFiltros(): void {
        let resultado = [...this.metricas];
        if (this.filtros.nivelRiesgo) { resultado = resultado.filter(m => m.nivelRiesgo === this.filtros.nivelRiesgo); }
        if (this.filtros.empleadoId) { resultado = resultado.filter(m => m.empleadoId === this.filtros.empleadoId); }
        this.metricasFiltradas = resultado;
    }

    limpiarFiltros(): void { this.filtros = { nivelRiesgo: '', empleadoId: null }; this.aplicarFiltros(); }

    verDetalle(id: number): void { this.router.navigate(['/bienestar/metricas', id]); }

    eliminar(id: number): void {
        if (confirm('¿Estás seguro de eliminar esta métrica?')) {
            this.metricaService.eliminar(id).subscribe({ next: () => { this.cargarMetricas(); }, error: (err) => { alert('Error al eliminar'); console.error(err); } });
        }
    }

    getColorRiesgo(nivel: string): string { const colores: any = { 'BAJO': '#22c55e', 'MEDIO': '#f59e0b', 'ALTO': '#ef4444' }; return colores[nivel] || '#64748b'; }
    getIconoRiesgo(nivel: string): string { const iconos: any = { 'BAJO': '🟢', 'MEDIO': '🟡', 'ALTO': '🔴' }; return iconos[nivel] || '📊'; }
    getMaterialIconoRiesgo(nivel: string): string { const iconos: any = { 'BAJO': 'check_circle', 'MEDIO': 'warning', 'ALTO': 'error' }; return iconos[nivel] || 'analytics'; }

    get totalMetricas(): number { return this.metricasFiltradas.length; }
    get metricasAltas(): number { return this.metricasFiltradas.filter(m => m.nivelRiesgo === 'ALTO').length; }
    get metricasMedias(): number { return this.metricasFiltradas.filter(m => m.nivelRiesgo === 'MEDIO').length; }
    get metricasBajas(): number { return this.metricasFiltradas.filter(m => m.nivelRiesgo === 'BAJO').length; }
}