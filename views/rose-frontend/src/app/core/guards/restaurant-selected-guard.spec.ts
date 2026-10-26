import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { restaurantSelectedGuard } from './restaurant-selected-guard';

describe('restaurantSelectedGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => restaurantSelectedGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
