import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar" [class.collapsed]="collapsed()">
      <div class="sidebar-header">
        <div class="logo-section" (click)="toggleCollapse()">
          <span class="logo-icon">HR</span>
          @if (!collapsed()) {
            <span class="logo-text">SaaSRRHH</span>
          }
        </div>
      </div>
      <nav class="sidebar-nav">
        @for (item of getFilteredMenuItems(); track item.route) {
          <a
            class="nav-item"
            [routerLink]="item.route"
            routerLinkActive="active"
            [routerLinkActiveOptions]="{ exact: item.route === '/dashboard' }"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            @if (!collapsed()) {
              <span class="nav-label">{{ item.label }}</span>
            }
          </a>
        }
      </nav>
      <div class="sidebar-footer">
        <button class="nav-item logout-btn" (click)="logout()">
          <span class="nav-icon">🚪</span>
          @if (!collapsed()) {
            <span class="nav-label">Cerrar Sesión</span>
          }
        </button>
      </div>
    </aside>
  `,
  styles: [
    `
      .sidebar {
        width: 260px;
        height: 100vh;
        background: #1a1a2e;
        color: white;
        display: flex;
        flex-direction: column;
        transition: width 0.3s ease;
        position: fixed;
        left: 0;
        top: 0;
        z-index: 1000;
      }
      .sidebar.collapsed {
        width: 64px;
      }
      .sidebar-header {
        padding: 1rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      }
      .logo-section {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        cursor: pointer;
      }
      .logo-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
        width: 36px;
        height: 36px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 0.85rem;
        flex-shrink: 0;
      }
      .logo-text {
        font-size: 1.1rem;
        font-weight: 600;
      }
      .sidebar-nav {
        flex: 1;
        padding: 0.75rem;
        overflow-y: auto;
      }
      .nav-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.7rem 0.85rem;
        border-radius: 8px;
        color: rgba(255, 255, 255, 0.7);
        text-decoration: none;
        transition: all 0.2s;
        margin-bottom: 2px;
        cursor: pointer;
        border: none;
        background: none;
        width: 100%;
        font-size: 0.95rem;
      }
      .nav-item:hover {
        background: rgba(255, 255, 255, 0.1);
        color: white;
      }
      .nav-item.active {
        background: rgba(102, 126, 234, 0.3);
        color: white;
      }
      .nav-icon {
        font-size: 1.2rem;
        width: 24px;
        text-align: center;
        flex-shrink: 0;
      }
      .nav-label {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .sidebar-footer {
        padding: 0.75rem;
        border-top: 1px solid rgba(255, 255, 255, 0.1);
      }
      .logout-btn:hover {
        background: rgba(255, 77, 77, 0.2) !important;
        color: #ff6b6b !important;
      }
    `,
  ],
})
export class SidebarComponent {
  collapsed = signal(false);

  menuItems: MenuItem[] = [
    {
      label: 'Dashboard',
      icon: '📊',
      route: '/dashboard',
      roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'],
    },
    { label: 'Empleados', icon: '👥', route: '/empleados', roles: ['ADMIN', 'SUPERVISOR'] },
    { label: 'Áreas de Trabajo', icon: '🏢', route: '/areas-trabajo', roles: ['ADMIN'] },
    {
      label: 'Tareas',
      icon: '✅',
      route: '/tareas',
      roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'],
    },
    {
      label: 'Asistencia',
      icon: '⏰',
      route: '/asistencias',
      roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'],
    },
    { label: 'Nómina', icon: '💰', route: '/nomina', roles: ['ADMIN'] },
    { label: 'Boletas', icon: '🧾', route: '/boletas', roles: ['ADMIN', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Planillas', icon: '📋', route: '/planillas', roles: ['ADMIN'] },
    {
      label: 'Incidentes',
      icon: '⚠️',
      route: '/reportes-incidentes',
      roles: ['ADMIN', 'SUPERVISOR'],
    },
    {
      label: 'Reportes Diarios',
      icon: '📝',
      route: '/reportes-diarios',
      roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'],
    },
    {
      label: 'Documentos',
      icon: '📄',
      route: '/documentos',
      roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'],
    },
    {
      label: 'Bienestar',
      icon: '💚',
      route: '/encuestas-bienestar',
      roles: ['EMPLEADO', 'TRABAJADOR', 'ADMIN'],
    },
    { label: 'Feedback', icon: '💬', route: '/feedback', roles: ['EMPLEADO', 'TRABAJADOR'] },
    { label: 'Usuarios', icon: '🔐', route: '/usuarios', roles: ['ADMIN'] },
    { label: 'Roles', icon: '🎯', route: '/roles', roles: ['ADMIN'] },
  ];

  constructor(private authService: AuthService) {}

  getFilteredMenuItems(): MenuItem[] {
    return this.menuItems.filter((item) =>
      item.roles.some((role) => this.authService.hasRole(role)),
    );
  }

  toggleCollapse(): void {
    this.collapsed.update((v) => !v);
  }

  logout(): void {
    this.authService.logout();
    window.location.href = '/auth/login';
  }
}
