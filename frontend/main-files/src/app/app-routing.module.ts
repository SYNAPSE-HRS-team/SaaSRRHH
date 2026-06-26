// src/app/app-routing.module.ts
import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard'; // ← IMPORTADO
import { ContentLayoutComponent } from './layouts/content/content-layout.component';
import { FullLayoutComponent } from './layouts/full/full-layout.component';
import { CONTENT_ROUTES } from './shared/routes/content-layout.routes';
import { Full_ROUTES } from './shared/routes/full-layout.routes';

const routes: Routes = [
  // Redirigir a login por defecto
  { path: '', redirectTo: '/auth/sign-in', pathMatch: 'full' }, // ← ESTO ES CLAVE

  // ========== RUTAS DE AUTENTICACIÓN (sin layout) ==========
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
  },

  // ========== RUTAS CON LAYOUT FULL (protegidas) ==========
  {
    path: '',
    component: FullLayoutComponent,
    canActivate: [AuthGuard], // ← AQUÍ ESTÁ EL GUARD
    children: Full_ROUTES,
  },

  // ========== RUTAS CON LAYOUT CONTENT (públicas) ==========
  {
    path: '',
    component: ContentLayoutComponent,
    children: CONTENT_ROUTES,
  },

  // Wildcard
  { path: '**', redirectTo: '/auth/sign-in' },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
