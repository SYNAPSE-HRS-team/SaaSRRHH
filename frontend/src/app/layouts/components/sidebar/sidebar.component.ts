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

  menuItems: MenuItem[] = [
    // Todos los roles
    { label: 'Dashboard', icon: '📊', route: '/dashboard', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    
    // Solo ADMIN y SUPERVISOR
    { label: 'Empleados', icon: '👥', route: '/empleados', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Solo ADMIN
    { label: 'Áreas de Trabajo', icon: '🏢', route: '/areas-trabajo', roles: ['ADMIN'] },
    
    // Todos los roles
    { label: 'Tareas', icon: '✅', route: '/tareas', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    { label: 'Asistencia', icon: '⏰', route: '/asistencias', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    
    // Solo ADMIN
    { label: 'Nómina', icon: '💰', route: '/nomina', roles: ['ADMIN'] },
    
    // ADMIN y TRABAJADOR
    { label: 'Boletas', icon: '🧾', route: '/boletas', roles: ['ADMIN', 'TRABAJADOR'] },
    
    // Solo ADMIN y SUPERVISOR
    { label: 'Incidentes', icon: '⚠️', route: '/reportes-incidentes', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Todos los roles
    { label: 'Reportes Diarios', icon: '📝', route: '/reportes-diarios', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    
    // Solo ADMIN y SUPERVISOR
    { label: 'Documentos', icon: '📄', route: '/documentos', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Solo ADMIN y SUPERVISOR (ellos gestionan encuestas)
    { label: 'Bienestar', icon: '💚', route: '/bienestar', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Todos los roles
    { label: 'Feedback', icon: '💬', route: '/feedback', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    
    // Solo ADMIN
    { label: 'Métricas Burnout', icon: '🧠', route: '/bienestar/metricas-burnout', roles: ['ADMIN'] },
    { label: 'Usuarios', icon: '🔐', route: '/usuarios', roles: ['ADMIN'] },
  ];

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.getCurrentUser();
    effect(() => { this.collapseChange.emit(this.collapsed()); });
  }

  getFilteredMenuItems(): MenuItem[] {
    return this.menuItems.filter(item => item.roles.some(role => this.authService.hasRole(role)));
  }

  getInitials(): string {
    const email = this.currentUser?.email || '';
    const parts = email.split('@')[0].split(/[._-]/);
    if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
    return email.substring(0, 2).toUpperCase();
  }

  toggleCollapse(): void { this.collapsed.update(v => !v); }

  onNavClick(): void {
    if (window.innerWidth <= 1024) { this.mobileClose.emit(); }
  }

  logout(): void {
    this.authService.logout();
    window.location.href = '/auth/login';
  }
}