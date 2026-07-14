import { DatePipe, NgIf } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { EmpleadoResponse } from '../../../../core/models/empleado.model';
import { AuthService } from '../../../../core/services/auth.service';
import { EmpleadoService } from '../../../../core/services/empleado.service';

@Component({
  selector: 'app-empleado-list',
  standalone: true,
  imports: [RouterLink, DatePipe, MatIconModule, NgIf],
  templateUrl: './empleado-list.component.html',
  styleUrls: ['./empleado-list.component.scss'],
})
export class EmpleadoListComponent implements OnInit {
  empleados = signal<EmpleadoResponse[]>([]);
  filteredEmpleados = signal<EmpleadoResponse[]>([]);
  cargosDisponibles: string[] = [];
  loading = signal(false);
  error = signal('');
  searchTerm = '';
  filterEstado = '';
  filterCargo = '';

  // Delete modal
  showDeleteModal = signal(false);
  empleadoToDelete: EmpleadoResponse | null = null;
  deleting = signal(false);

  // Detail modal
  showDetailModal = signal(false);
  empleadoDetalle: EmpleadoResponse | null = null;

  isAdmin = false;

  constructor(
    private empleadoService: EmpleadoService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.hasRole('ADMIN');
    console.log('isAdmin =', this.isAdmin);
    this.loadEmpleados();
  }

  loadEmpleados(): void {
    this.loading.set(true);
    this.error.set('');

    this.empleadoService.getAll().subscribe({
      next: (data) => {
        this.empleados.set(data);
        this.applyFilters();
        this.cargosDisponibles = [...new Set(data.map((e) => e.cargo).filter(Boolean) as string[])];
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Error al cargar empleados: ' + (err.error?.message || err.message));
        this.loading.set(false);
      },
    });
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    this.applyFilters();
  }

  onFilterEstado(event: Event): void {
    this.filterEstado = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  onFilterCargo(event: Event): void {
    this.filterCargo = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  private applyFilters(): void {
    let result = this.empleados();

    if (this.searchTerm) {
      result = result.filter(
        (e) =>
          e.nombres.toLowerCase().includes(this.searchTerm) ||
          e.apellidos.toLowerCase().includes(this.searchTerm) ||
          e.dni.toLowerCase().includes(this.searchTerm) ||
          (e.cargo || '').toLowerCase().includes(this.searchTerm),
      );
    }

    if (this.filterEstado) {
      result = result.filter((e) => e.activo === (this.filterEstado === 'true'));
    }

    if (this.filterCargo) {
      result = result.filter((e) => e.cargo === this.filterCargo);
    }

    this.filteredEmpleados.set(result);
  }

  verDetalle(emp: EmpleadoResponse): void {
    this.empleadoDetalle = emp;
    this.showDetailModal.set(true);
  }

  confirmarEliminar(emp: EmpleadoResponse): void {
    this.empleadoToDelete = emp;
    this.showDeleteModal.set(true);
  }

  eliminarEmpleado(): void {
    if (!this.empleadoToDelete) return;
    this.deleting.set(true);

    this.empleadoService.delete(this.empleadoToDelete.id).subscribe({
      next: () => {
        this.showDeleteModal.set(false);
        this.deleting.set(false);
        this.empleadoToDelete = null;
        this.loadEmpleados();
      },
      error: (err) => {
        this.deleting.set(false);
        alert('Error al eliminar empleado: ' + (err.error?.message || err.message));
      },
    });
  }
}
