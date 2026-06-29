import { Component, Input, Output, EventEmitter, signal, effect } from '@angular/core';
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
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {

  @Input() mobileOpen = false;
  @Output() mobileClose = new EventEmitter<void>();
  @Output() collapseChange = new EventEmitter<boolean>();

  collapsed = signal(false);
  currentUser: ReturnType<AuthService['getCurrentUser']>;

  // Menú fusionado: Se añadieron 'Tareas' y se unificaron las rutas y roles
  // Mantiene tu estructura base y añade los items de la otra persona.
  menuItems: MenuItem[] = [
    { label: 'Dashboard', icon: '📊', route: '/dashboard', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Empleados', icon: '👥', route: '/empleados', roles: ['ADMIN', 'SUPERVISOR'] },
    { label: 'Áreas de Trabajo', icon: '🏢', route: '/areas-trabajo', roles: ['ADMIN'] },
    // Item añadido de la otra versión
    { label: 'Tareas', icon: '✅', route: '/tareas', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Asistencia', icon: '⏰', route: '/asistencias', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Nómina', icon: '💰', route: '/nomina', roles: ['ADMIN'] },
    { label: 'Planillas', icon: '📋', route: '/planillas', roles: ['ADMIN'] },
    { label: 'Boletas', icon: '🧾', route: '/boletas', roles: ['ADMIN', 'EMPLEADO', 'TRABAJADOR'] },
    // Ruta unificada a '/incidentes' como en tu versión original
    { label: 'Incidentes', icon: '⚠️', route: '/incidentes', roles: ['ADMIN', 'SUPERVISOR'] },
    { label: 'Reportes Diarios', icon: '📝', route: '/reportes-diarios', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Documentos', icon: '📄', route: '/documentos', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO', 'TRABAJADOR'] },
    { label: 'Bienestar', icon: '💚', route: '/encuestas-bienestar', roles: ['EMPLEADO', 'TRABAJADOR', 'ADMIN'] },
    { label: 'Feedback', icon: '💬', route: '/feedback', roles: ['EMPLEADO', 'TRABAJADOR'] },
    { label: 'Usuarios', icon: '🔐', route: '/usuarios', roles: ['ADMIN'] },
    { label: 'Roles', icon: '🎯', route: '/roles', roles: ['ADMIN'] },
  ];

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.getCurrentUser();

    // Sincroniza signal → output
    effect(() => {
      this.collapseChange.emit(this.collapsed());
    });
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
    // Cerrar sidebar en móvil al navegar
    if (window.innerWidth <= 1024) {
      this.mobileClose.emit();
    }
  }

  logout(): void {
    this.authService.logout();
    window.location.href = '/auth/login';
  }
}