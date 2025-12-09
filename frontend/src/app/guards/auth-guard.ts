// authGuard.ts

import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth';
 // Adjust path as needed

export const authGuard: CanActivateFn = (route, state): Observable<boolean | UrlTree> | boolean | UrlTree => {
  // 1. Inject necessary services
  const authService = inject(AuthService);
  const router = inject(Router);

  // 2. Get the authentication status Observable
  return authService.isLoggedIn$.pipe(
    map(isLoggedIn => {
      if (isLoggedIn) {
        // 3. User is logged in: Allow navigation
        return true;
      } else {
        // 4. User is NOT logged in: Block navigation and redirect
        // The router.createUrlTree creates a URL object representing the login page
        console.log('Access denied. Redirecting to login.');
        return router.createUrlTree(['/login']); 
      }
    })
  );
};