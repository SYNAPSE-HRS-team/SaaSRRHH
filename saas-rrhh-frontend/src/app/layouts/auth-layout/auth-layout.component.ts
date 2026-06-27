import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="logo-icon">HR</div>
          <h1>SaaSRRHH</h1>
          <p class="subtitle">Sistema de Gestión de RRHH</p>
        </div>
        <router-outlet />
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 1rem;
    }
    .auth-card {
      background: white;
      border-radius: 16px;
      padding: 2.5rem;
      width: 100%;
      max-width: 420px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.15);
    }
    .auth-header {
      text-align: center;
      margin-bottom: 2rem;
    }
    .logo-icon {
      width: 56px;
      height: 56px;
      margin: 0 auto 0.5rem;
      background: linear-gradient(135deg, #667eea, #764ba2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: 700;
      font-size: 1.2rem;
    }
    .auth-header h1 {
      margin: 0;
      color: #1a1a2e;
      font-size: 1.75rem;
      font-weight: 700;
    }
    .subtitle {
      color: #666;
      margin: 0.25rem 0 0;
      font-size: 0.9rem;
    }
  `]
})
export class AuthLayoutComponent {}
