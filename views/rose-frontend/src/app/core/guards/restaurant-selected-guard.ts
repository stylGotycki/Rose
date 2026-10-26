import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const restaurantSelectedGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const restaurantId = route.paramMap.get('restaurantId');

  if (restaurantId) {
    return true;
  }

  return router.createUrlTree(['/app/restaurants']);
};