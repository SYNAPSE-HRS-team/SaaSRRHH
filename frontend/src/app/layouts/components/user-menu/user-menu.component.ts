import { Component, signal, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-user-menu',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './user-menu.component.html',
  styleUrls: ['./user-menu.component.scss']
})
export class UserMenuComponent {
  isOpen = signal(false);
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  get currentUser() {
    return this.authService.getCurrentUser();
  }

  getDisplayName(): string {
    const user = this.currentUser;
    if (user?.nombre && user?.apellido) {
      return `${user.nombre} ${user.apellido}`;
    }
    return user?.email || 'Usuario';
  }

  getInitials(): string {
    const user = this.currentUser;
    if (user?.nombre && user?.apellido) {
      return (user.nombre[0] + user.apellido[0]).toUpperCase();
    }
    const email = user?.email || '';
    const parts = email.split('@')[0].split(/[._-]/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return email.substring(0, 2).toUpperCase();
  }

  toggleMenu(event: Event): void {
    event.stopPropagation();
    this.isOpen.update(v => !v);
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.isOpen.set(false);
  }

  goToProfile(): void {
    this.isOpen.set(false);
    this.router.navigate(['/perfil']);  // ✅ Ruta corregida
  }

  logout(): void {
    this.isOpen.set(false);
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  switchAccount(): void {
    this.isOpen.set(false);
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}