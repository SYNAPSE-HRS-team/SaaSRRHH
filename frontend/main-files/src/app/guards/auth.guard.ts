import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/auth/sign-in']);
      return false;
    }

    const allowedRoles = route.data?.['roles'] as string[] | undefined;
    if (allowedRoles?.length && !this.authService.hasRole(...allowedRoles)) {
      this.router.navigate([this.authService.getPrimaryRoute()]);
      return false;
    }

    return true;
  }
}
