import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NominaService } from '../../../../core/services/nomina.service';
import { BoletaPagoService } from '../../../../core/services/boleta-pago.service';
import { Planilla, getMesNombre, MESES_NOMBRE } from '../../../../core/models/planilla.model';
import { BoletaPago } from '../../../../core/models/boleta-pago.model';

@Component({
  selector: 'app-nomina',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './nomina.component.html',
  styleUrls: ['./nomina.component.scss']
})
export class NominaComponent implements OnInit {
  planillas = signal<Planilla[]>([]);
  filteredPlanillas = signal<Planilla[]>([]);
  boletas = signal<BoletaPago[]>([]);
  loadingPlanillas = signal(false);
  loadingBoletas = signal(false);
  generando = signal(false);
  cerrando = signal(false);
  descargandoPdf = signal<number | null>(null);
  error = signal('');
  success = signal('');
  generarError = signal('');

  showGenerarModal = signal(false);
  showBoletasModal = signal(false);
  showCerrarModal = signal(false);
  planillaSeleccionada: Planilla | null = null;
  planillaACerrar: Planilla | null = null;

  generarMes = new Date().getMonth() + 1;
  generarAnio = new Date().getFullYear();
  filtroEstado = '';

  meses = MESES_NOMBRE;
  getMesNombre = getMesNombre;

  constructor(
    private nominaService: NominaService,
    private boletaService: BoletaPagoService
  ) {}

  ngOnInit(): void {
    this.loadPlanillas();
  }

  loadPlanillas(): void {
    this.loadingPlanillas.set(true);
    this.error.set('');
    this.nominaService.listarPlanillas().subscribe({
      next: (data) => {
        // Ordenar por año desc, mes desc
        const sorted = data.sort((a, b) => b.anio !== a.anio ? b.anio - a.anio : b.mes - a.mes);
        this.planillas.set(sorted);
        this.applyFilter();
        this.loadingPlanillas.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar planillas');
        this.loadingPlanillas.set(false);
      }
    });
  }

  applyFilter(): void {
    const all = this.planillas();
    this.filteredPlanillas.set(
      this.filtroEstado ? all.filter(p => p.estado === this.filtroEstado) : all
    );
  }

  onFiltroEstado(event: Event): void {
    this.filtroEstado = (event.target as HTMLSelectElement).value;
    this.applyFilter();
  }

  generarPlanilla(): void {
    this.generarError.set('');
    this.generando.set(true);
    this.nominaService.generarPlanilla(this.generarMes, this.generarAnio).subscribe({
      next: (planilla) => {
        this.generando.set(false);
        this.showGenerarModal.set(false);
        this.success.set(`✅ Planilla de ${getMesNombre(planilla.mes)} ${planilla.anio} generada correctamente.`);
        setTimeout(() => this.success.set(''), 4000);
        this.loadPlanillas();
      },
      error: (err) => {
        this.generando.set(false);
        this.generarError.set(err.error?.message || 'Error al generar la planilla');
      }
    });
  }

  verBoletas(planilla: Planilla): void {
    this.planillaSeleccionada = planilla;
    this.boletas.set([]);
    this.showBoletasModal.set(true);
    this.loadingBoletas.set(true);
    // Cargar todas las boletas y filtrar por planillaId
    this.boletaService.listar().subscribe({
      next: (data) => {
        const filtradas = data.filter(b => b.planillaId === planilla.id);
        this.boletas.set(filtradas);
        this.loadingBoletas.set(false);
      },
      error: () => {
        this.loadingBoletas.set(false);
      }
    });
  }

  confirmarCerrar(p: Planilla): void {
    this.planillaACerrar = p;
    this.showCerrarModal.set(true);
  }

  cerrarPlanilla(): void {
    if (!this.planillaACerrar?.id) return;
    this.cerrando.set(true);
    this.error.set('');
    this.nominaService.cerrarPlanilla(this.planillaACerrar.id).subscribe({
      next: () => {
        this.cerrando.set(false);
        this.showCerrarModal.set(false);
        this.planillaACerrar = null;
        this.success.set('✅ Planilla cerrada correctamente.');
        setTimeout(() => this.success.set(''), 4000);
        this.loadPlanillas();
      },
      error: () => {
        this.cerrando.set(false);
        this.error.set('Error al cerrar la planilla');
        this.showCerrarModal.set(false);
      }
    });
  }

  descargarPdf(id: number): void {
    this.descargandoPdf.set(id);
    this.boletaService.descargarPdf(id).subscribe({
      next: (blob) => {
        this.boletaService.saveBlob(blob, `boleta-${id}.pdf`);
        this.descargandoPdf.set(null);
      },
      error: () => {
        this.descargandoPdf.set(null);
        this.error.set('No se pudo descargar el PDF de la boleta.');
        setTimeout(() => this.error.set(''), 3000);
      }
    });
  }

  closeGenerarModal(): void {
    if (!this.generando()) {
      this.showGenerarModal.set(false);
      this.generarError.set('');
    }
  }

  countByEstado(estado: string): number {
    return this.planillas().filter(p => (p.estado ?? 'PROCESADO') === estado).length;
  }

  getUltimaNomina(): string {
    const p = this.planillas()[0];
    if (!p) return '—';
    return `${getMesNombre(p.mes)} ${p.anio}`;
  }

  getTotalBoletas(): number {
    return this.boletas().reduce((sum, b) => sum + (b.netoPagar || 0), 0);
  }

  getEstadoBadge(estado?: string): string {
    return estado === 'CERRADO' ? 'badge badge-dark' : 'badge badge-primary';
  }
}