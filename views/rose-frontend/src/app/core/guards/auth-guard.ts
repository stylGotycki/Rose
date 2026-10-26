import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenStorage } from '../auth/token-storage';

export const authGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorage);
  const router = inject(Router);

  if (tokenStorage.isLoggedIn()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};