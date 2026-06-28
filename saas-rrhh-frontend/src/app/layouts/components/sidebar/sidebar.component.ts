import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
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
    <aside class="sidebar" [class.collapsed]="collapsed()" [class.mobile-open]="mobileOpen">
      <div class="sidebar-header">
        <div class="logo-section" (click)="toggleCollapse()">
          <div class="logo-icon">HR</div>
          @if (!collapsed()) {
            <span class="logo-text">SaaSRRHH</span>
            <button class="mobile-close-btn" (click)="$event.stopPropagation(); mobileClose.emit()" aria-label="Cerrar menú">
              ✕
            </button>
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
            (click)="onNavClick()"
            [title]="collapsed() ? item.label : ''"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            @if (!collapsed()) {
              <span class="nav-label">{{ item.label }}</span>
            }
          </a>
        }
      </nav>

      <div class="sidebar-footer">
        @if (!collapsed()) {
          <div class="user-mini">
            <div class="user-avatar-mini">{{ getInitials() }}</div>
            <div class="user-info-mini">
              <span class="user-email-mini">{{ currentUser?.email }}</span>
              <span class="user-role-mini">{{ currentUser?.rol }}</span>
            </div>
          </div>
        }
        <button class="nav-item logout-btn" (click)="logout()">
          <span class="nav-icon">🚪</span>
          @if (!collapsed()) {
            <span class="nav-label">Cerrar Sesión</span>
          }
        </button>
      </div>
    </aside>
  `,
  styles: [`
    /* ---- SIDEBAR BASE ---- */
    .sidebar {
      position: fixed;
      top: 0;
      left: 0;
      height: 100vh;
      width: var(--sidebar-width, 260px);
      background: var(--color-sidebar-bg, #0D1F3C);
      display: flex;
      flex-direction: column;
      z-index: 150;
      transition: width 0.3s ease, transform 0.3s ease;
      overflow: hidden;
      box-shadow: 2px 0 20px rgba(0,0,0,0.12);
    }

    .sidebar.collapsed {
      width: 64px;
    }

    /* ---- MOBILE ---- */
    @media (max-width: 768px) {
      .sidebar {
        transform: translateX(-100%);
        width: 280px !important;
        box-shadow: 4px 0 30px rgba(0,0,0,0.25);
      }
      .sidebar.mobile-open {
        transform: translateX(0);
      }
    }

    /* ---- HEADER ---- */
    .sidebar-header {
      padding: 0 0.875rem;
      height: var(--topbar-height, 64px);
      display: flex;
      align-items: center;
      border-bottom: 1px solid var(--color-sidebar-border, rgba(255,255,255,0.08));
      flex-shrink: 0;
    }

    .logo-section {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      cursor: pointer;
      width: 100%;
      user-select: none;
    }

    .logo-icon {
      width: 36px; height: 36px;
      background: linear-gradient(135deg, #2563EB, #1E3A5F);
      border-radius: 10px;
      display: flex; align-items: center; justify-content: center;
      color: white;
      font-weight: 800;
      font-size: 0.72rem;
      flex-shrink: 0;
      box-shadow: 0 4px 12px rgba(37,99,235,0.35);
    }

    .logo-text {
      font-size: 1.05rem;
      font-weight: 700;
      color: white;
      letter-spacing: -0.01em;
      flex: 1;
      white-space: nowrap;
    }

    .mobile-close-btn {
      display: none;
      background: rgba(255,255,255,0.08);
      border: none;
      color: rgba(255,255,255,0.6);
      cursor: pointer;
      font-size: 0.9rem;
      width: 28px; height: 28px;
      border-radius: 50%;
      align-items: center; justify-content: center;
      transition: all 0.2s;
      flex-shrink: 0;
      margin-left: auto;
    }
    .mobile-close-btn:hover { background: rgba(255,255,255,0.15); color: white; }

    @media (max-width: 768px) {
      .mobile-close-btn { display: flex; }
    }

    /* ---- NAV ---- */
    .sidebar-nav {
      flex: 1;
      padding: 0.625rem 0;
      overflow-y: auto;
      overflow-x: hidden;
      scrollbar-width: none;
    }
    .sidebar-nav::-webkit-scrollbar { display: none; }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.65rem 0.875rem;
      margin: 0.1rem 0.5rem;
      border-radius: 10px;
      color: var(--color-sidebar-text, #94A3B8);
      text-decoration: none;
      font-size: 0.855rem;
      font-weight: 500;
      transition: all 0.2s ease;
      white-space: nowrap;
      cursor: pointer;
      border: none;
      background: none;
      width: calc(100% - 1rem);
      text-align: left;
      position: relative;
    }

    .nav-item:hover {
      background: rgba(255,255,255,0.07);
      color: white;
      transform: translateX(2px);
    }

    .nav-item.active {
      background: linear-gradient(135deg, rgba(37,99,235,0.22), rgba(37,99,235,0.12));
      color: white;
      font-weight: 600;
    }
    .nav-item.active::before {
      content: '';
      position: absolute;
      left: -0.5rem;
      top: 0; bottom: 0;
      width: 3px;
      background: #2563EB;
      border-radius: 0 3px 3px 0;
    }

    .nav-icon {
      font-size: 1rem;
      width: 22px;
      display: flex;
      justify-content: center;
      flex-shrink: 0;
    }

    .nav-label { line-height: 1; flex: 1; }

    /* ---- FOOTER ---- */
    .sidebar-footer {
      padding: 0.75rem;
      border-top: 1px solid rgba(255,255,255,0.07);
      flex-shrink: 0;
    }

    .user-mini {
      display: flex;
      align-items: center;
      gap: 0.625rem;
      padding: 0.625rem 0.75rem;
      margin-bottom: 0.5rem;
      border-radius: 10px;
      background: rgba(255,255,255,0.04);
    }

    .user-avatar-mini {
      width: 32px; height: 32px;
      border-radius: 50%;
      background: linear-gradient(135deg, #2563EB, #1E3A5F);
      color: white;
      display: flex; align-items: center; justify-content: center;
      font-size: 0.72rem; font-weight: 700;
      flex-shrink: 0;
    }

    .user-info-mini { display: flex; flex-direction: column; min-width: 0; }
    .user-email-mini {
      font-size: 0.72rem;
      color: rgba(255,255,255,0.65);
      font-weight: 500;
      overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
    }
    .user-role-mini {
      font-size: 0.62rem;
      color: #60A5FA;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .logout-btn {
      color: rgba(255,255,255,0.45) !important;
    }
    .logout-btn:hover {
      background: rgba(220,38,38,0.15) !important;
      color: #FCA5A5 !important;
      transform: none !important;
    }

    /* ---- COLLAPSED ICON-ONLY TOOLTIPS ---- */
    .sidebar.collapsed .nav-item:hover::after {
      content: attr(title);
      position: absolute;
      left: calc(100% + 12px);
      top: 50%;
      transform: translateY(-50%);
      background: #1E3A5F;
      color: white;
      padding: 0.35rem 0.75rem;
      border-radius: 6px;
      font-size: 0.78rem;
      font-weight: 600;
      white-space: nowrap;
      pointer-events: none;
      box-shadow: var(--shadow-lg, 0 4px 12px rgba(0,0,0,0.15));
      z-index: 200;
    }
  `]
})
export class SidebarComponent {
  @Input() mobileOpen = false;
  @Output() mobileClose = new EventEmitter<void>();

  collapsed = signal(false);
  currentUser: ReturnType<AuthService['getCurrentUser']>;

  menuItems: MenuItem[] = [
    { label: 'Dashboard',        icon: '📊', route: '/dashboard',            roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Empleados',        icon: '👥', route: '/empleados',             roles: ['ADMIN', 'SUPERVISOR'] },
    { label: 'Áreas de Trabajo', icon: '🏢', route: '/areas-trabajo',         roles: ['ADMIN'] },
    { label: 'Asistencia',       icon: '⏰', route: '/asistencias',           roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Nómina',           icon: '💰', route: '/nomina',                roles: ['ADMIN'] },
    { label: 'Planillas',        icon: '📋', route: '/planillas',             roles: ['ADMIN'] },
    { label: 'Boletas',          icon: '🧾', route: '/boletas',               roles: ['ADMIN', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Incidentes',       icon: '⚠️', route: '/incidentes',            roles: ['ADMIN', 'SUPERVISOR'] },
    { label: 'Reportes Diarios', icon: '📝', route: '/reportes-diarios',      roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Documentos',       icon: '📄', route: '/documentos',            roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Bienestar',        icon: '💚', route: '/encuestas-bienestar',   roles: ['EMPLEADO', 'TRABAJADOR', 'ADMIN'] },
    { label: 'Feedback',         icon: '💬', route: '/feedback',              roles: ['EMPLEADO', 'TRABAJADOR'] },
    { label: 'Usuarios',         icon: '🔐', route: '/usuarios',              roles: ['ADMIN'] },
    { label: 'Roles',            icon: '🎯', route: '/roles',                 roles: ['ADMIN'] },
  ];

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.getCurrentUser();
  }

  getFilteredMenuItems(): MenuItem[] {
    return this.menuItems.filter(item =>
      item.roles.some(role => this.authService.hasRole(role))
    );
  }

  getInitials(): string {
    const email = this.currentUser?.email || '';
    const parts = email.split('@')[0].split(/[._-]/);
    if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
    return email.substring(0, 2).toUpperCase();
  }

  toggleCollapse(): void {
    this.collapsed.update(v => !v);
  }

  onNavClick(): void {
    if (window.innerWidth <= 768) {
      this.mobileClose.emit();
    }
  }

  logout(): void {
    this.authService.logout();
    window.location.href = '/auth/login';
  }
}
