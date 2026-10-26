import { DatePipe } from '@angular/common';
import {
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { LocationReadDto } from '../../../shared/models/location.model';
import { Location } from '../location';

@Component({
  selector: 'app-location-list-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './location-list-page.html',
  styleUrl: './location-list-page.css',
})
export class LocationListPage implements OnInit {
  private readonly locationService = inject(Location);

  readonly restaurantContext = inject(RestaurantContext);

  readonly locations = signal<LocationReadDto[]>([]);
  readonly loading = signal(true);
  readonly archivingId = signal<string | null>(null);

  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly locationToArchive = signal<LocationReadDto | null>(null);

  readonly activeLocations = computed(() =>
    this.locations().filter(
      (location) => location.status === 'ACTIVE',
    ),
  );

  readonly archivedLocations = computed(() =>
    this.locations().filter(
      (location) => location.status === 'ARCHIVED',
    ),
  );

  ngOnInit(): void {
    const message = history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

    this.loadLocations();
  }

  loadLocations(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.loading.set(false);
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.locationService
      .getAllByRestaurant(restaurantId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.locations.set(response.items ?? []);
        },
        error: (error) => {
          console.error('Location list error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania lokali.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać listy lokali.',
          );
        },
      });
  }

  createLink(): string[] {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return ['/app/restaurants'];
    }

    return [
      '/app/restaurants',
      restaurantId,
      'panel',
      'locations',
      'create',
    ];
  }

  editLink(locationId: string): string[] {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return ['/app/restaurants'];
    }

    return [
      '/app/restaurants',
      restaurantId,
      'panel',
      'locations',
      locationId,
      'edit',
    ];
  }

  showArchiveConfirmation(location: LocationReadDto): void {
    this.locationToArchive.set(location);
  }

  hideArchiveConfirmation(): void {
    if (this.archivingId()) {
      return;
    }

    this.locationToArchive.set(null);
  }

  archiveLocation(): void {
    const location = this.locationToArchive();

    if (!location) {
      return;
    }

    this.archivingId.set(location.id);
    this.errorMessage.set(null);

    this.locationService
      .archive(location.id)
      .pipe(finalize(() => this.archivingId.set(null)))
      .subscribe({
        next: () => {
          this.locations.update((locations) =>
            locations.map((item) =>
              item.id === location.id
                ? {
                    ...item,
                    status: 'ARCHIVED',
                    updatedAt: new Date().toISOString(),
                  }
                : item,
            ),
          );

          this.locationToArchive.set(null);

          this.showSuccess(
            `Lokal „${location.name}” został przeniesiony do archiwum.`,
          );
        },
        error: (error) => {
          console.error('Location archive error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do archiwizacji lokalu.',
            );
            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego lokalu.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się zarchiwizować lokalu.',
          );
        },
      });
  }

  private showSuccess(message: string): void {
    this.successMessage.set(message);

    window.setTimeout(() => {
      if (this.successMessage() === message) {
        this.successMessage.set(null);
      }
    }, 4000);
  }
}