import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BoletaPagoService } from '../../../../core/services/boleta-pago.service';
import { AuthService } from '../../../../core/services/auth.service';
import { BoletaPago } from '../../../../core/models/boleta-pago.model';
import { getMesNombre, MESES_NOMBRE } from '../../../../core/models/planilla.model';

@Component({
  selector: 'app-boleta-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './boleta-list.component.html',
  styleUrls: ['./boleta-list.component.scss']
})
export class BoletaListComponent implements OnInit {
  boletas = signal<BoletaPago[]>([]);
  filtered = signal<BoletaPago[]>([]);
  loading = signal(false);
  descargandoPdf = signal<number | null>(null);
  error = signal('');
  boletaDetalle = signal<BoletaPago | null>(null);

  searchTerm = '';
  filterPlanilla = '';
  getMesNombre = getMesNombre;
  esAdmin = false;

  constructor(
    private boletaService: BoletaPagoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.esAdmin = this.authService.hasRole('ADMIN');
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    const obs = this.esAdmin
      ? this.boletaService.listar()
      : this.boletaService.listarMisBoletas();
    obs.subscribe({
      next: data => {
        this.boletas.set(data);
        this.applyFilter();
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'Error al cargar boletas');
        this.loading.set(false);
      }
    });
  }

  applyFilter(): void {
    let result = this.boletas();
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(b =>
        b.empleadoId?.toString().includes(term) ||
        b.planillaId?.toString().includes(term) ||
        b.id?.toString().includes(term)
      );
    }
    if (this.filterPlanilla) {
      result = result.filter(b => b.planillaId === +this.filterPlanilla);
    }
    this.filtered.set(result);
  }

  onSearch(e: Event): void { this.searchTerm = (e.target as HTMLInputElement).value; this.applyFilter(); }
  onFilterPlanilla(e: Event): void { this.filterPlanilla = (e.target as HTMLSelectElement).value; this.applyFilter(); }

  planillasDisponibles(): number[] {
    return [...new Set(this.boletas().map(b => b.planillaId).filter(Boolean) as number[])].sort((a, b) => b - a);
  }

  getTotalBonos(b: BoletaPago): number {
    return (b.asignacionFamiliar || 0) + (b.bonoBeta || 0) + (b.horasExtraPago || 0) + (b.otrosBonos || 0);
  }

  getTotalDescuentos(b: BoletaPago): number {
    return (b.descuentoInasistencia || 0) + (b.otrosDescuentos || 0);
  }

  getTotalNeto(): number {
    return this.boletas().reduce((s, b) => s + (b.netoPagar || 0), 0);
  }

  getPromedioNeto(): number {
    const total = this.boletas().length;
    return total > 0 ? this.getTotalNeto() / total : 0;
  }

  getEmpleadosUnicos(): number {
    return new Set(this.boletas().map(b => b.empleadoId)).size;
  }

  descargarPdf(id: number): void {
    this.descargandoPdf.set(id);
    this.boletaService.descargarPdf(id).subscribe({
      next: blob => {
        this.boletaService.saveBlob(blob, `boleta-${id}.pdf`);
        this.descargandoPdf.set(null);
      },
      error: () => {
        this.descargandoPdf.set(null);
        this.error.set('No se pudo descargar el PDF.');
        setTimeout(() => this.error.set(''), 3000);
      }
    });
  }
}