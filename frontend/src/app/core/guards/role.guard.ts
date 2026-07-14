import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];
    
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    const user = this.authService.getCurrentUser();
    
    if (!user) {
      this.router.navigate(['/auth/login']);
      return false;
    }

    const userRole = user.rol?.toUpperCase() || '';
    
    const hasAccess = requiredRoles.some(role => {
      const required = role.toUpperCase();
      return userRole === required || userRole === `ROLE_${required}`;
    });

    if (hasAccess) {
      return true;
    }

    this.router.navigate(['/dashboard']);
    return false;
  }
}