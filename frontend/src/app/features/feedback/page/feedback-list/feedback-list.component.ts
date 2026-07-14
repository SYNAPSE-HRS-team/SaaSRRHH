import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FeedbackService } from '../../../../core/services/feedback.service';
import { AuthService } from '../../../../core/services/auth.service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-feedback-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './feedback-list.component.html',
  styleUrls: ['./feedback-list.component.scss']
})
export class FeedbackListComponent implements OnInit {
  feedbacks: any[] = [];
  feedbacksFiltrados: any[] = [];
  loading = false;
  filtroEstado = '';
  filtroCategoria = '';
  showModal = false;
  showCreateModal = false;
  showDetailModal = false;
  selectedFeedback: any = null;
  respuesta = '';
  estadoRespuesta = 'REVISADO';
  nuevaCategoria = 'CLIMA_LABORAL';
  nuevoMensaje = '';
  esAnonimo = false;
  isAdmin = false;

  constructor(private feedbackService: FeedbackService, private authService: AuthService) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.getCurrentUser()?.rol === 'ADMIN';
    this.cargarFeedbacks();
  }

  cargarFeedbacks(): void {
    this.loading = true;
    const user = this.authService.getCurrentUser();
    if (this.isAdmin) {
      this.feedbackService.listar().subscribe({
        next: (data) => { this.feedbacks = data; this.aplicarFiltros(); this.loading = false; },
        error: () => { this.loading = false; }
      });
    } else {
      this.feedbackService.listarMisFeedbacks(user?.idUsuario || 0).subscribe({
        next: (data) => { this.feedbacks = data; this.aplicarFiltros(); this.loading = false; },
        error: () => { this.loading = false; }
      });
    }
  }

  aplicarFiltros(): void {
    let resultado = [...this.feedbacks];
    if (this.filtroEstado) resultado = resultado.filter(f => f.estado === this.filtroEstado);
    if (this.filtroCategoria) resultado = resultado.filter(f => f.categoria === this.filtroCategoria);
    this.feedbacksFiltrados = resultado;
  }

  limpiarFiltros(): void { this.filtroEstado = ''; this.filtroCategoria = ''; this.aplicarFiltros(); }

  abrirCrear(): void { this.nuevoMensaje = ''; this.nuevaCategoria = 'CLIMA_LABORAL'; this.esAnonimo = false; this.showCreateModal = true; }

  crearFeedback(): void {
    if (!this.nuevoMensaje.trim()) return;
    const user = this.authService.getCurrentUser();
    this.feedbackService.enviarFeedback({
      mensaje: this.nuevoMensaje,
      categoria: this.nuevaCategoria,
      empleadoId: user?.idUsuario || undefined,
      esAnonimo: this.esAnonimo
    }).subscribe({
      next: () => { this.showCreateModal = false; this.cargarFeedbacks(); },
      error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo enviar'))
    });
  }

  verDetalle(fb: any): void { this.selectedFeedback = fb; this.showDetailModal = true; }

  abrirResponder(fb: any): void { this.selectedFeedback = fb; this.respuesta = ''; this.estadoRespuesta = 'REVISADO'; this.showModal = true; }

  enviarRespuesta(): void {
    if (!this.respuesta.trim()) return;
    this.feedbackService.responderFeedback(this.selectedFeedback.id, this.respuesta, this.estadoRespuesta).subscribe({
      next: () => { this.showModal = false; this.cargarFeedbacks(); },
      error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo enviar'))
    });
  }

  get pendientes(): number { return this.feedbacks.filter(f => f.estado === 'PENDIENTE').length; }
  get respondidos(): number { return this.feedbacks.filter(f => f.estado !== 'PENDIENTE').length; }
}