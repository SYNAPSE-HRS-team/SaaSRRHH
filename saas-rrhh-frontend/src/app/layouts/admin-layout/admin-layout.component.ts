import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
    @if (mobileSidebarOpen()) {
      <div class="mobile-overlay" (click)="mobileSidebarOpen.set(false)"></div>
    }

    <div class="admin-container">
      <app-sidebar
        [mobileOpen]="mobileSidebarOpen()"
        (mobileClose)="mobileSidebarOpen.set(false)"
      />
      <main class="main-content">
        <header class="topbar">
          <div class="topbar-left">
            <button
              class="hamburger"
              (click)="mobileSidebarOpen.set(!mobileSidebarOpen())"
              aria-label="Abrir menú"
              id="hamburger-btn"
            >
              <span [class.open]="mobileSidebarOpen()"></span>
              <span [class.open]="mobileSidebarOpen()"></span>
              <span [class.open]="mobileSidebarOpen()"></span>
            </button>
            <div class="brand-mobile">
              <div class="brand-icon">HR</div>
              <span class="brand-name">SaaSRRHH</span>
            </div>
          </div>

          <div class="topbar-right">
            <div class="user-chip">
              <div class="user-avatar">{{ getInitials() }}</div>
              <div class="user-details">
                <span class="user-name">{{ currentUser?.email }}</span>
                <span class="user-role">{{ currentUser?.rol }}</span>
              </div>
            </div>
          </div>
        </header>

        <div class="content-wrapper">
          <router-outlet />
        </div>
      </main>
    </div>
  `,
  styles: [`
    :host { display: block; }

    .mobile-overlay {
      position: fixed;
      inset: 0;
      background: rgba(13, 31, 60, 0.55);
      z-index: 149;
      backdrop-filter: blur(3px);
      animation: fadeOverlay 0.2s ease;
    }
    @keyframes fadeOverlay { from { opacity: 0; } to { opacity: 1; } }

    .admin-container {
      display: flex;
      min-height: 100vh;
      background: var(--color-bg, #F0F4F8);
    }

    .main-content {
      margin-left: var(--sidebar-width, 260px);
      flex: 1;
      display: flex;
      flex-direction: column;
      min-width: 0;
      transition: margin-left 0.3s ease;
    }

    /* ---- TOP BAR ---- */
    .topbar {
      background: #fff;
      padding: 0 1.5rem;
      height: var(--topbar-height, 64px);
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: 0 1px 0 #E2E8F0;
      position: sticky;
      top: 0;
      z-index: 100;
      gap: 1rem;
    }

    .topbar-left {
      display: flex;
      align-items: center;
      gap: 0.875rem;
    }

    /* Hamburger */
    .hamburger {
      display: none;
      flex-direction: column;
      justify-content: center;
      gap: 5px;
      background: none;
      border: none;
      cursor: pointer;
      padding: 0.5rem;
      border-radius: 8px;
      transition: background 0.2s;
      width: 36px; height: 36px;
    }
    .hamburger:hover { background: var(--color-bg, #F0F4F8); }
    .hamburger span {
      display: block;
      height: 2px;
      background: var(--color-text, #1A202C);
      border-radius: 2px;
      transition: all 0.25s ease;
      width: 22px;
    }
    .hamburger span:first-child.open { transform: translateY(7px) rotate(45deg); }
    .hamburger span:nth-child(2).open { opacity: 0; transform: scaleX(0); }
    .hamburger span:last-child.open { transform: translateY(-7px) rotate(-45deg); }

    /* Mobile brand */
    .brand-mobile {
      display: none;
      align-items: center;
      gap: 0.5rem;
    }
    .brand-icon {
      width: 32px; height: 32px;
      background: linear-gradient(135deg, #1E3A5F, #2563EB);
      color: white;
      border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
      font-size: 0.7rem; font-weight: 800;
      box-shadow: 0 2px 8px rgba(37,99,235,0.3);
    }
    .brand-name {
      font-weight: 700;
      font-size: 1rem;
      color: var(--color-primary, #1E3A5F);
      letter-spacing: -0.01em;
    }

    /* User chip */
    .topbar-right { display: flex; align-items: center; }

    .user-chip {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.4rem 0.875rem 0.4rem 0.4rem;
      border-radius: var(--radius-full, 9999px);
      border: 1px solid var(--color-border, #E2E8F0);
      cursor: default;
      transition: background 0.2s;
    }
    .user-chip:hover { background: var(--color-bg, #F0F4F8); }

    .user-avatar {
      width: 36px; height: 36px;
      border-radius: 50%;
      background: linear-gradient(135deg, #1E3A5F, #2563EB);
      color: white;
      display: flex; align-items: center; justify-content: center;
      font-size: 0.78rem; font-weight: 700;
      flex-shrink: 0;
    }

    .user-details {
      display: flex;
      flex-direction: column;
    }

    .user-name {
      font-size: 0.82rem;
      font-weight: 600;
      color: var(--color-text, #1A202C);
      max-width: 180px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      line-height: 1.2;
    }

    .user-role {
      font-size: 0.68rem;
      font-weight: 700;
      color: var(--color-accent, #2563EB);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    /* ---- CONTENT ---- */
    .content-wrapper {
      padding: 1.75rem;
      flex: 1;
    }

    /* ======================================================
       RESPONSIVE
    ====================================================== */
    @media (max-width: 768px) {
      .main-content {
        margin-left: 0;
      }
      .hamburger {
        display: flex;
      }
      .brand-mobile {
        display: flex;
      }
      .content-wrapper {
        padding: 1rem;
      }
      .user-details {
        display: none;
      }
      .user-chip {
        padding: 0.3rem;
        border: none;
      }
    }

    @media (max-width: 480px) {
      .topbar {
        padding: 0 0.875rem;
      }
      .content-wrapper {
        padding: 0.75rem;
      }
    }
  `]
})
export class AdminLayoutComponent {
  currentUser: ReturnType<AuthService['getCurrentUser']>;
  mobileSidebarOpen = signal(false);

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.getCurrentUser();
  }

  getInitials(): string {
    const email = this.currentUser?.email || '';
    const parts = email.split('@')[0].split(/[._-]/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return email.substring(0, 2).toUpperCase();
  }
}
