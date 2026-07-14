import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanillaService } from '../../../../core/services/planilla.service';
import { Planilla, PlanillaRequest, EstadoPlanilla, getMesNombre, MESES_NOMBRE } from '../../../../core/models/planilla.model';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-planilla-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './planilla-list.component.html',
  styleUrls: ['./planilla-list.component.scss']
})
export class PlanillaListComponent implements OnInit {
  planillas = signal<Planilla[]>([]);
  filtered = signal<Planilla[]>([]);
  loading = signal(false);
  saving = signal(false);
  deleting = signal(false);
  error = signal('');
  success = signal('');
  formError = signal('');
  showForm = signal(false);
  showDelete = signal(false);

  editando = false;
  editId: number | null = null;
  planillaToDelete: Planilla | null = null;

  filterEstado = '';
  filterAnio = '';

  form: Partial<PlanillaRequest> & { estado?: EstadoPlanilla; fechaCierre?: string } = this.emptyForm();
  meses = MESES_NOMBRE;
  getMesNombre = getMesNombre;

  constructor(private planillaService: PlanillaService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.planillaService.listar().subscribe({
      next: data => {
        const sorted = data.sort((a, b) => b.anio !== a.anio ? b.anio - a.anio : b.mes - a.mes);
        this.planillas.set(sorted);
        this.applyFilter();
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'Error al cargar planillas');
        this.loading.set(false);
      }
    });
  }

  applyFilter(): void {
    let result = this.planillas();
    if (this.filterEstado) result = result.filter(p => (p.estado ?? 'PROCESADO') === this.filterEstado);
    if (this.filterAnio) result = result.filter(p => p.anio === +this.filterAnio);
    this.filtered.set(result);
  }

  onFilterEstado(e: Event): void { this.filterEstado = (e.target as HTMLSelectElement).value; this.applyFilter(); }
  onFilterAnio(e: Event): void { this.filterAnio = (e.target as HTMLSelectElement).value; this.applyFilter(); }

  aniosDisponibles(): number[] {
    return [...new Set(this.planillas().map(p => p.anio))].sort((a, b) => b - a);
  }

  countEstado(estado: string): number {
    return this.planillas().filter(p => (p.estado ?? 'PROCESADO') === estado).length;
  }

  openForm(planilla: Planilla | null): void {
    this.formError.set('');
    if (planilla) {
      this.editando = true;
      this.editId = planilla.id ?? null;
      this.form = {
        mes: planilla.mes,
        anio: planilla.anio,
        totalPagado: planilla.totalPagado,
        estado: planilla.estado ?? 'PROCESADO',
        fechaCierre: planilla.fechaCierre?.slice(0, 10)
      };
    } else {
      this.editando = false;
      this.editId = null;
      this.form = this.emptyForm();
    }
    this.showForm.set(true);
  }

  closeForm(): void {
    if (!this.saving()) this.showForm.set(false);
  }

  guardar(): void {
    if (!this.form.mes || !this.form.anio) {
      this.formError.set('Mes y año son obligatorios');
      return;
    }
    this.saving.set(true);
    this.formError.set('');
    const req$ = this.editando && this.editId
      ? this.planillaService.actualizar(this.editId, this.form as PlanillaRequest)
      : this.planillaService.guardar(this.form as PlanillaRequest);

    req$.subscribe({
      next: () => {
        this.saving.set(false);
        this.showForm.set(false);
        this.success.set(this.editando ? 'Planilla actualizada correctamente.' : 'Planilla creada correctamente.');
        setTimeout(() => this.success.set(''), 4000);
        this.load();
      },
      error: err => {
        this.saving.set(false);
        this.formError.set(err.error?.message || 'Error al guardar la planilla');
      }
    });
  }

  confirmarEliminar(p: Planilla): void {
    this.planillaToDelete = p;
    this.showDelete.set(true);
  }

  eliminar(): void {
    if (!this.planillaToDelete?.id) return;
    this.deleting.set(true);
    this.planillaService.eliminar(this.planillaToDelete.id).subscribe({
      next: () => {
        this.deleting.set(false);
        this.showDelete.set(false);
        this.planillaToDelete = null;
        this.success.set('Planilla eliminada.');
        setTimeout(() => this.success.set(''), 3000);
        this.load();
      },
      error: err => {
        this.deleting.set(false);
        this.error.set(err.error?.message || 'Error al eliminar la planilla');
        this.showDelete.set(false);
      }
    });
  }

  private emptyForm(): Partial<PlanillaRequest> & { estado: EstadoPlanilla } {
    return {
      mes: new Date().getMonth() + 1,
      anio: new Date().getFullYear(),
      totalPagado: undefined,
      estado: 'PROCESADO',
      fechaCierre: undefined
    };
  }
}