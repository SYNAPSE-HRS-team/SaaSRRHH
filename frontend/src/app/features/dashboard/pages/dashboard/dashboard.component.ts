import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';
import { AnaliticaService, DashboardDTO } from '../../../../core/services/analitica.service';

interface ActionItem {
  icon: string;
  label: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  user: ReturnType<AuthService['getCurrentUser']>;
  isAdmin = false;
  isSupervisor = false;
  isTrabajador = false;
  rolLabel = '';

  loading = signal(false);
  dashboardData = signal<DashboardDTO | null>(null);

  allActions: ActionItem[] = [
    { icon: '👥', label: 'Gestionar Empleados', route: '/empleados', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: '🏢', label: 'Áreas de Trabajo', route: '/areas-trabajo', roles: ['ADMIN'] },
    { icon: '✅', label: 'Ver Tareas', route: '/tareas', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    { icon: '⏰', label: 'Registrar Asistencia', route: '/asistencias', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    { icon: '💰', label: 'Nómina', route: '/nomina', roles: ['ADMIN'] },
    { icon: '🧾', label: 'Mis Boletas', route: '/boletas', roles: ['ADMIN', 'TRABAJADOR'] },
    { icon: '⚠️', label: 'Reportar Incidente', route: '/reportes-incidentes', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: '📝', label: 'Reportes Diarios', route: '/reportes-diarios', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    { icon: '📄', label: 'Documentos', route: '/documentos', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: '💚', label: 'Bienestar', route: '/bienestar', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: '💬', label: 'Feedback', route: '/feedback', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    { icon: '🔐', label: 'Usuarios', route: '/usuarios', roles: ['ADMIN'] }
  ];

  get filteredActions(): ActionItem[] {
    return this.allActions.filter(a => a.roles.includes(this.user?.rol ?? ''));
  }

  constructor(
    private authService: AuthService,
    private analiticaService: AnaliticaService
  ) {
    this.user = this.authService.getCurrentUser();
    this.setRoleFlags();
  }

  ngOnInit(): void {
    this.loadDashboard();
  }

  private setRoleFlags(): void {
    const rol = this.user?.rol ?? '';
    this.isAdmin = rol === 'ADMIN';
    this.isSupervisor = rol === 'SUPERVISOR';
    this.isTrabajador = rol === 'TRABAJADOR';

    const roleLabels: Record<string, string> = {
      'ADMIN': 'Administrador',
      'SUPERVISOR': 'Supervisor',
      'TRABAJADOR': 'Trabajador'
    };
    this.rolLabel = roleLabels[rol] || rol;
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.analiticaService.obtenerDashboard().subscribe({
      next: (data) => {
        this.dashboardData.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  getRiskClass(nivel?: string): string {
    const map: Record<string, string> = {
      'BAJO': 'bajo',
      'MEDIO': 'medio',
      'ALTO': 'alto',
    };
    return map[nivel?.toUpperCase() ?? ''] || '';
  }
}