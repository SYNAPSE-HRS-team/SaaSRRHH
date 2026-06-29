import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AreaTrabajoService } from '../../../../core/services/area-trabajo.service';
import { AreaTrabajo } from '../../../../core/models/area-trabajo.model';

@Component({
  selector: 'app-area-list',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './area-list.component.html',
  styleUrls: ['./area-list.component.scss']
})
export class AreaListComponent implements OnInit {
  areas = signal<AreaTrabajo[]>([]);
  filteredAreas = signal<AreaTrabajo[]>([]);
  loading = signal(false);
  error = signal('');
  searchTerm = '';

  showDeleteModal = signal(false);
  areaToDelete: AreaTrabajo | null = null;
  deleting = signal(false);

  constructor(
    private areaService: AreaTrabajoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAreas();
  }

  loadAreas(): void {
    this.loading.set(true);
    this.error.set('');

    this.areaService.getAll().subscribe({
      next: (data) => {
        this.areas.set(data);
        this.applyFilter();
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Error al cargar áreas: ' + (err.error?.message || err.message));
        this.loading.set(false);
      }
    });
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    this.applyFilter();
  }

  private applyFilter(): void {
    if (!this.searchTerm) {
      this.filteredAreas.set(this.areas());
      return;
    }
    this.filteredAreas.set(
      this.areas().filter(a => a.nombre.toLowerCase().includes(this.searchTerm))
    );
  }

  editarArea(area: AreaTrabajo): void {
    this.router.navigate(['/areas-trabajo/editar', area.id]);
  }

  confirmarEliminar(area: AreaTrabajo): void {
    this.areaToDelete = area;
    this.showDeleteModal.set(true);
  }

  eliminarArea(): void {
    if (!this.areaToDelete?.id) return;
    this.deleting.set(true);

    this.areaService.delete(this.areaToDelete.id).subscribe({
      next: () => {
        this.showDeleteModal.set(false);
        this.deleting.set(false);
        this.areaToDelete = null;
        this.loadAreas();
      },
      error: (err) => {
        this.deleting.set(false);
        alert('Error al eliminar área: ' + (err.error?.message || err.message));
      }
    });
  }
}