import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
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
  imports: [RouterLink],
  template: `
    <div class="dashboard">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1>Dashboard</h1>
          <p class="welcome">
            Bienvenido, {{ user?.email }}
            <span class="role-badge">{{ rolLabel }}</span>
          </p>
        </div>
        <div class="header-actions">
          <button class="btn-refresh" (click)="loadDashboard()" [disabled]="loading()">
            <span>🔄</span> Actualizar
          </button>
        </div>
      </div>

      @if (loading()) {
        <div class="loading-state">
          <div class="spinner-lg"></div>
          <p>Cargando dashboard...</p>
        </div>
      } @else {
        <!-- ADMIN Dashboard -->
        @if (isAdmin) {
          <section class="dashboard-section">
            <h2 class="section-title">📊 Panorama General</h2>
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon blue">👥</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.totalEmpleados ?? '—' }}</span>
                  <span class="stat-label">Total Empleados</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon purple">🔐</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.totalUsuarios ?? '—' }}</span>
                  <span class="stat-label">Usuarios del Sistema</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon green">📝</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.reportesDiarios ?? '—' }}</span>
                  <span class="stat-label">Reportes Diarios</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon orange">⚠️</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.incidentes ?? '—' }}</span>
                  <span class="stat-label">Incidentes Reportados</span>
                </div>
              </div>
            </div>
          </section>

          <section class="dashboard-section">
            <h2 class="section-title">📈 Métricas de Riesgo</h2>
            <div class="metrics-grid">
              <div class="metric-card">
                <div class="metric-header">
                  <span class="metric-icon">⏳</span>
                  <span class="metric-title">Ausentismo</span>
                </div>
                <div class="metric-value">{{ dashboardData()?.ausencias ?? 0 }}</div>
                <div class="metric-sub">Total ausencias registradas</div>
              </div>
              <div class="metric-card">
                <div class="metric-header">
                  <span class="metric-icon">📊</span>
                  <span class="metric-title">% Ausentismo</span>
                </div>
                <div class="metric-value" [class.text-danger]="(dashboardData()?.porcentajeAusentismo ?? 0) > 10">
                  {{ dashboardData()?.porcentajeAusentismo?.toFixed(1) ?? '0.0' }}%
                </div>
                <div class="metric-sub">Porcentaje del período</div>
              </div>
              <div class="metric-card">
                <div class="metric-header">
                  <span class="metric-icon">🛡️</span>
                  <span class="metric-title">Nivel de Riesgo</span>
                </div>
                <div class="metric-value">
                  <span class="risk-badge" [class]="getRiskClass(dashboardData()?.nivelRiesgo)">
                    {{ dashboardData()?.nivelRiesgo ?? '—' }}
                  </span>
                </div>
                <div class="metric-sub">Riesgo organizacional</div>
              </div>
            </div>
          </section>
        }

        <!-- SUPERVISOR Dashboard -->
        @if (isSupervisor) {
          <section class="dashboard-section">
            <h2 class="section-title">📋 Resumen de Equipo</h2>
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon blue">👥</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.totalEmpleados ?? '—' }}</span>
                  <span class="stat-label">Empleados a Cargo</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon green">📝</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.reportesDiarios ?? '—' }}</span>
                  <span class="stat-label">Reportes del Equipo</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon orange">⚠️</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.incidentes ?? '—' }}</span>
                  <span class="stat-label">Incidentes</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon red">⏳</div>
                <div class="stat-info">
                  <span class="stat-value">{{ dashboardData()?.ausencias ?? '—' }}</span>
                  <span class="stat-label">Ausencias Hoy</span>
                </div>
              </div>
            </div>
          </section>
        }

        <!-- EMPLEADO Dashboard -->
        @if (isEmpleado) {
          <section class="dashboard-section">
            <h2 class="section-title">👤 Mi Espacio</h2>
            <div class="welcome-card">
              <div class="welcome-icon">👋</div>
              <div class="welcome-text">
                <h3>¡Bienvenido, {{ user?.email }}!</h3>
                <p>Accede a tus tareas, registra tu asistencia y revisa tus boletas desde aquí.</p>
              </div>
            </div>
          </section>
        }

        <!-- Quick Actions (filtradas por rol) -->
        <section class="dashboard-section">
          <h2 class="section-title">⚡ Acciones Rápidas</h2>
          <div class="actions-grid">
            @for (action of filteredActions; track action.route) {
              <a [routerLink]="action.route" class="action-card">
                <span class="action-icon">{{ action.icon }}</span>
                <span class="action-label">{{ action.label }}</span>
              </a>
            }
          </div>
        </section>
      }
    </div>
  `,
  styles: [`
    .dashboard { max-width: 1200px; }

    .page-header {
      display: flex; justify-content: space-between; align-items: flex-start;
      margin-bottom: 2rem;
    }
    .page-header h1 { margin: 0; color: #1a1a2e; font-size: 1.75rem; }
    .welcome { color: #666; margin: 0.25rem 0 0; display: flex; align-items: center; gap: 0.75rem; }
    .role-badge {
      display: inline-block; padding: 0.2rem 0.6rem; border-radius: 12px;
      font-size: 0.75rem; font-weight: 600;
      background: linear-gradient(135deg, #667eea, #764ba2); color: white;
    }
    .header-actions { display: flex; gap: 0.75rem; }

    .btn-refresh {
      display: inline-flex; align-items: center; gap: 0.5rem;
      padding: 0.6rem 1.2rem; background: white; color: #555;
      border: 2px solid #e0e0e0; border-radius: 8px;
      font-size: 0.85rem; font-weight: 500; cursor: pointer; transition: all 0.2s;
    }
    .btn-refresh:hover:not(:disabled) { border-color: #667eea; color: #667eea; }
    .btn-refresh:disabled { opacity: 0.6; cursor: not-allowed; }

    .loading-state {
      display: flex; flex-direction: column; align-items: center;
      justify-content: center; padding: 4rem; background: white;
      border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
    .spinner-lg {
      width: 40px; height: 40px;
      border: 3px solid #e0e0e0; border-top-color: #667eea;
      border-radius: 50%; animation: spin 0.6s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }

    .dashboard-section { margin-bottom: 2rem; }
    .section-title { font-size: 1.15rem; color: #1a1a2e; margin: 0 0 1rem; font-weight: 600; }

    .stats-grid {
      display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      gap: 1rem;
    }
    .stat-card {
      background: white; border-radius: 12px; padding: 1.25rem;
      display: flex; align-items: center; gap: 1rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
      transition: transform 0.2s, box-shadow 0.2s;
    }
    .stat-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,0.1); }

    .stat-icon {
      width: 48px; height: 48px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center;
      font-size: 1.4rem; flex-shrink: 0;
    }
    .stat-icon.blue { background: #eef2ff; }
    .stat-icon.green { background: #ecfdf5; }
    .stat-icon.purple { background: #f5f3ff; }
    .stat-icon.orange { background: #fff7ed; }
    .stat-icon.red { background: #fef2f2; }

    .stat-info { display: flex; flex-direction: column; }
    .stat-value { font-size: 1.5rem; font-weight: 700; color: #1a1a2e; }
    .stat-label { font-size: 0.8rem; color: #666; }

    .metrics-grid {
      display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
    }
    .metric-card {
      background: white; border-radius: 12px; padding: 1.25rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
      transition: transform 0.2s;
    }
    .metric-card:hover { transform: translateY(-2px); }
    .metric-header { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.75rem; }
    .metric-icon { font-size: 1.2rem; }
    .metric-title { font-size: 0.85rem; font-weight: 600; color: #555; }
    .metric-value { font-size: 1.75rem; font-weight: 700; color: #1a1a2e; margin-bottom: 0.25rem; }
    .metric-sub { font-size: 0.8rem; color: #888; }
    .text-danger { color: #dc2626; }
    .risk-badge {
      display: inline-block; padding: 0.25rem 0.75rem; border-radius: 12px;
      font-size: 0.85rem; font-weight: 600;
    }
    .risk-badge.bajo { background: #ecfdf5; color: #059669; }
    .risk-badge.medio { background: #fffbeb; color: #d97706; }
    .risk-badge.alto { background: #fef2f2; color: #dc2626; }
    .risk-badge.critico { background: #fef2f2; color: #991b1b; }

    .welcome-card {
      background: linear-gradient(135deg, #667eea, #764ba2);
      border-radius: 16px; padding: 2rem; display: flex;
      align-items: center; gap: 1.5rem; color: white;
    }
    .welcome-icon { font-size: 3rem; }
    .welcome-text h3 { margin: 0 0 0.5rem; font-size: 1.3rem; }
    .welcome-text p { margin: 0; opacity: 0.9; font-size: 0.95rem; }

    .actions-grid {
      display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
      gap: 0.85rem;
    }
    .action-card {
      background: white; border-radius: 12px; padding: 1.25rem 1rem;
      display: flex; flex-direction: column; align-items: center;
      gap: 0.6rem; text-decoration: none;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
      transition: all 0.2s; cursor: pointer;
    }
    .action-card:hover {
      transform: translateY(-3px); box-shadow: 0 6px 20px rgba(0,0,0,0.1);
    }
    .action-icon { font-size: 1.8rem; }
    .action-label { font-size: 0.85rem; font-weight: 600; color: #333; text-align: center; }
  `]
})
export class DashboardComponent implements OnInit {
  user: ReturnType<AuthService['getCurrentUser']>;
  isAdmin = false;
  isSupervisor = false;
  isEmpleado = false;
  rolLabel = '';

  loading = signal(false);
  dashboardData = signal<DashboardDTO | null>(null);

  allActions: ActionItem[] = [
    { icon: '👥', label: 'Gestionar Empleados', route: '/empleados', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: '🏢', label: 'Áreas de Trabajo', route: '/areas-trabajo', roles: ['ADMIN'] },
    { icon: '✅', label: 'Ver Tareas', route: '/tareas', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { icon: '⏰', label: 'Registrar Asistencia', route: '/asistencias', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { icon: '💰', label: 'Nómina', route: '/nomina', roles: ['ADMIN'] },
    { icon: '🧾', label: 'Mis Boletas', route: '/boletas', roles: ['ADMIN', 'EMPLEADO', 'TRABAJADOR'] },
    { icon: '📋', label: 'Planillas', route: '/planillas', roles: ['ADMIN'] },
    { icon: '⚠️', label: 'Reportar Incidente', route: '/incidentes', roles: ['ADMIN', 'SUPERVISOR'] },
    { icon: '📝', label: 'Reportes Diarios', route: '/reportes-diarios', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { icon: '📄', label: 'Documentos', route: '/documentos', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { icon: '💚', label: 'Encuesta Bienestar', route: '/encuestas-bienestar', roles: ['EMPLEADO', 'TRABAJADOR'] },
    { icon: '💬', label: 'Feedback Anónimo', route: '/feedback', roles: ['EMPLEADO', 'TRABAJADOR'] },
    { icon: '🔐', label: 'Usuarios', route: '/usuarios', roles: ['ADMIN'] },
    { icon: '🎯', label: 'Roles', route: '/roles', roles: ['ADMIN'] },
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
    this.isEmpleado = rol === 'EMPLEADO' || rol === 'TRABAJADOR';

    const roleLabels: Record<string, string> = {
      'ADMIN': 'Administrador',
      'SUPERVISOR': 'Supervisor',
      'EMPLEADO': 'Empleado',
      'TRABAJADOR': 'Empleado'
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
        // Si falla la API, mostrar datos vacíos (no bloquear)
      }
    });
  }

  getRiskClass(nivel?: string): string {
    const map: Record<string, string> = {
      'BAJO': 'bajo',
      'MEDIO': 'medio',
      'ALTO': 'alto',
      'CRÍTICO': 'critico',
      'CRITICO': 'critico'
    };
    return map[nivel?.toUpperCase() ?? ''] || '';
  }
}
