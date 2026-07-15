import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
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
  imports: [RouterLink, CommonModule, MatIconModule],
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
    { icon: 'people', label: 'Gestionar Empleados', route: '/empleados', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: 'business', label: 'Áreas de Trabajo', route: '/areas-trabajo', roles: ['ADMIN'] },
    { icon: 'assignment_turned_in', label: 'Ver Tareas', route: '/tareas', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR', 'EMPLEADO'] },
    { icon: 'schedule', label: 'Registrar Asistencia', route: '/asistencias', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR', 'EMPLEADO'] },
    { icon: 'payments', label: 'Nómina', route: '/nomina', roles: ['ADMIN'] },
    { icon: 'receipt_long', label: 'Mis Boletas', route: '/boletas', roles: ['ADMIN', 'TRABAJADOR', 'EMPLEADO'] },
    { icon: 'report_problem', label: 'Reportar Incidente', route: '/reportes-incidentes', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: 'description', label: 'Reportes Diarios', route: '/reportes-diarios', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR', 'EMPLEADO'] },
    { icon: 'article', label: 'Documentos', route: '/documentos', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: 'health_and_safety', label: 'Bienestar', route: '/bienestar', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: 'forum', label: 'Feedback', route: '/feedback', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR', 'EMPLEADO'] },
    { icon: 'admin_panel_settings', label: 'Usuarios', route: '/usuarios', roles: ['ADMIN'] }
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
    this.isTrabajador = rol === 'TRABAJADOR' || rol === 'EMPLEADO';

    const roleLabels: Record<string, string> = {
      'ADMIN': 'Administrador',
      'SUPERVISOR': 'Supervisor',
      'TRABAJADOR': 'Trabajador',
      'EMPLEADO': 'Empleado'
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