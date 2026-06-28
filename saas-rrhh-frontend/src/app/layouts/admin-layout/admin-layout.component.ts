import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
    <div class="admin-container">
      <app-sidebar />
      <main class="main-content">
        <header class="topbar">
          <div class="topbar-left">
            <h2>{{ currentUser?.email }}</h2>
          </div>
          <div class="topbar-right">
            <span class="role-badge">{{ currentUser?.rol }}</span>
            <span class="user-email">{{ currentUser?.email }}</span>
          </div>
        </header>
        <div class="content-wrapper">
          <router-outlet />
        </div>
      </main>
    </div>
  `,
  styles: [`
    .admin-container {
      display: flex;
      min-height: 100vh;
      background: #f5f6fa;
    }
    .main-content {
      margin-left: 260px;
      flex: 1;
      display: flex;
      flex-direction: column;
      transition: margin-left 0.3s ease;
    }
    .topbar {
      background: white;
      padding: 1rem 2rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: 0 1px 3px rgba(0,0,0,0.08);
      position: sticky;
      top: 0;
      z-index: 100;
    }
    .topbar-left h2 {
      margin: 0;
      font-size: 1.25rem;
      color: #1a1a2e;
    }
    .topbar-right {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .role-badge {
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      padding: 0.25rem 0.75rem;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 500;
    }
    .user-email {
      color: #666;
      font-size: 0.9rem;
    }
    .content-wrapper {
      padding: 2rem;
      flex: 1;
    }
  `]
})
export class AdminLayoutComponent {
  currentUser: ReturnType<AuthService['getCurrentUser']>;

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.getCurrentUser();
  }
}
