import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AreaTrabajoService } from '../../../../core/services/area-trabajo.service';
import { AreaTrabajo } from '../../../../core/models/area-trabajo.model';

@Component({
  selector: 'app-area-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './area-form.component.html',
  styleUrls: ['./area-form.component.scss']
})
export class AreaFormComponent implements OnInit {
  formData: AreaTrabajo = {
    nombre: '',
    cultivoTipo: '',
    activo: true
  };

  isEditing = false;
  areaId: number | null = null;
  saving = signal(false);
  error = signal('');
  successMessage = signal('');

  constructor(
    private areaService: AreaTrabajoService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditing = true;
      this.areaId = +id;
      this.loadArea(this.areaId);
    }
  }

  loadArea(id: number): void {
    this.areaService.getById(id).subscribe({
      next: (area) => {
        this.formData = { ...area };
      },
      error: () => this.error.set('Error al cargar el área')
    });
  }

  onSubmit(): void {
    if (!this.formData.nombre?.trim()) {
      this.error.set('El nombre del área es obligatorio');
      return;
    }

    this.saving.set(true);
    this.error.set('');

    const request = this.isEditing && this.areaId
      ? this.areaService.update(this.areaId, this.formData)
      : this.areaService.create(this.formData);

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.successMessage.set(`Área "${this.formData.nombre}" ${this.isEditing ? 'actualizada' : 'creada'} exitosamente`);
        setTimeout(() => this.router.navigate(['/areas-trabajo']), 1500);
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message || 'Error al guardar el área');
      }
    });
  }
}