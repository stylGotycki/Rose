import { Injectable, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';

import { RestaurantReadDto } from '../../shared/models/restaurant.model';
import { Restaurant } from '../../features/restaurants/restaurant';

@Injectable({
  providedIn: 'root',
})
export class RestaurantContext {
  private readonly restaurantService = inject(Restaurant);

  readonly restaurant = signal<RestaurantReadDto | null>(null);
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly restaurantId = computed(
    () => this.restaurant()?.id ?? null,
  );

  readonly restaurantName = computed(
    () => this.restaurant()?.name ?? 'Restauracja',
  );

  load(restaurantId: string): void {
    const currentRestaurant = this.restaurant();

    if (currentRestaurant?.id === restaurantId) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.restaurantService
      .getById(restaurantId)
      .pipe(
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (restaurant) => {
          this.restaurant.set(restaurant);
        },
        error: (error) => {
          console.error('Restaurant context error:', error);

          this.restaurant.set(null);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do tej restauracji.',
            );
            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono restauracji.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać danych restauracji.',
          );
        },
      });
  }

  updateLocal(restaurant: RestaurantReadDto): void {
    this.restaurant.set(restaurant);
  }

  clear(): void {
    this.restaurant.set(null);
    this.errorMessage.set(null);
    this.loading.set(false);
  }
}