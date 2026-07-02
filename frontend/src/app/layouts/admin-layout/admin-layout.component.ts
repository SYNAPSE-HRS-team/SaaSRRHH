import { Component, signal, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { UserMenuComponent } from '../components/user-menu/user-menu.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, UserMenuComponent],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit, OnDestroy {
  currentUser: ReturnType<AuthService['getCurrentUser']>;
  
  mobileSidebarOpen = signal(false);
  isSidebarCollapsed = signal(false);
  
  // Propiedades para la hora
  currentTime: string = '';
  currentDate: string = '';
  private timeInterval: any;

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    this.updateDateTime();
    // Actualizar cada segundo
    this.timeInterval = setInterval(() => {
      this.updateDateTime();
    }, 1000);
  }

  ngOnDestroy(): void {
    // Limpiar intervalo al destruir el componente
    if (this.timeInterval) {
      clearInterval(this.timeInterval);
    }
  }

  private updateDateTime(): void {
    const now = new Date();
    
    // Formato hora: HH:MM:SS
    this.currentTime = now.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    });
    
    // Formato fecha: Lunes, 28 de Junio de 2026
    this.currentDate = now.toLocaleDateString('es-PE', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getInitials(): string {
    const email = this.currentUser?.email || '';
    const parts = email.split('@')[0].split(/[._-]/);
    if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
    return email.substring(0, 2).toUpperCase();
  }

  // Método para cerrar sesión (opcional, por si se necesita desde el layout)
  logout(): void {
    this.authService.logout();
    // La redirección se maneja en el UserMenuComponent
  }
}