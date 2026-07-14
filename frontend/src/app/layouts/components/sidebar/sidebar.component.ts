import { Component, Input, Output, EventEmitter, signal, effect } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
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
  imports: [RouterLink, RouterLinkActive, MatIconModule],
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
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard', roles: ['ADMIN', 'SUPERVISOR', 'TRABAJADOR'] },
    
    // Solo ADMIN y SUPERVISOR
    { label: 'Empleados', icon: 'people', route: '/empleados', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Solo ADMIN
    { label: 'Áreas de Trabajo', icon: 'business', route: '/areas-trabajo', roles: ['ADMIN'] },
    
    // Todos los roles
    { label: 'Tareas', icon: 'assignment_turned_in', route: '/tareas', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO'] },
    { label: 'Asistencia', icon: 'schedule', route: '/asistencias', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO'] },
    
    // Solo ADMIN
    { label: 'Nómina', icon: 'payments', route: '/nomina', roles: ['ADMIN'] },
    
    // ADMIN y TRABAJADOR
    { label: 'Boletas', icon: 'receipt_long', route: '/boletas', roles: ['ADMIN', 'EMPLEADO'] },
    
    // Solo ADMIN y SUPERVISOR
    { label: 'Incidentes', icon: 'report_problem', route: '/reportes-incidentes', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Todos los roles
    { label: 'Reportes Diarios', icon: 'description', route: '/reportes-diarios', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO'] },
    
    // Solo ADMIN y SUPERVISOR
    { label: 'Documentos', icon: 'article', route: '/documentos', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Solo ADMIN y SUPERVISOR (ellos gestionan encuestas)
    { label: 'Bienestar', icon: 'health_and_safety', route: '/bienestar/encuestas', roles: ['ADMIN', 'SUPERVISOR'] },
    
    // Todos los roles
    { label: 'Feedback', icon: 'forum', route: '/feedback', roles: ['ADMIN', 'SUPERVISOR', 'EMPLEADO'] },
    
    // Solo ADMIN
    { label: 'Métricas Burnout', icon: 'psychology', route: '/bienestar/metricas-burnout', roles: ['ADMIN'] },
    { label: 'Usuarios', icon: 'admin_panel_settings', route: '/usuarios', roles: ['ADMIN'] },
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