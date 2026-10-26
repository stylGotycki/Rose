import {
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { RestaurantReadDto } from '../../../shared/models/restaurant.model';
import { Restaurant } from '../restaurant';

@Component({
  selector: 'app-restaurant-list-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './restaurant-list-page.html',
  styleUrl: './restaurant-list-page.css',
})
export class RestaurantListPage implements OnInit {
  private readonly restaurantService = inject(Restaurant);

  readonly restaurants = signal<RestaurantReadDto[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  readonly activeRestaurants = computed(() =>
    this.restaurants().filter(
      (restaurant) => restaurant.status === 'ACTIVE',
    ),
  );

  readonly archivedRestaurants = computed(() =>
    this.restaurants().filter(
      (restaurant) => restaurant.status === 'ARCHIVED',
    ),
  );

  ngOnInit(): void {
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.restaurantService.getAll().subscribe({
      next: (response) => {
        this.restaurants.set(response.items ?? []);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Restaurant list error:', error);

        this.loading.set(false);

        if (error.status === 401 || error.status === 403) {
          this.errorMessage.set(
            'Nie masz dostępu do listy restauracji.',
          );
          return;
        }

        this.errorMessage.set(
          'Nie udało się pobrać restauracji.',
        );
      },
    });
  }
}