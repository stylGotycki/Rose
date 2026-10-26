import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { Location } from '../location';

@Component({
  selector: 'app-location-create-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './location-create-page.html',
  styleUrl: './location-create-page.css',
})
export class LocationCreatePage {
  private readonly locationService = inject(Location);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  name = '';
  city = '';
  street = '';
  buildingNumber = '';
  postalCode = '';

  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);

  listLink(): string[] {
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
    ];
  }

  submit(): void {
    this.errorMessage.set(null);

    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );
      return;
    }

    const name = this.name.trim();
    const city = this.city.trim();
    const street = this.street.trim();
    const buildingNumber = this.buildingNumber.trim();
    const postalCode = this.postalCode.trim();

    if (
      !name ||
      !city ||
      !street ||
      !buildingNumber ||
      !postalCode
    ) {
      this.errorMessage.set(
        'Uzupełnij wszystkie wymagane pola.',
      );
      return;
    }

    if (
      name.length > 128 ||
      city.length > 128 ||
      street.length > 128 ||
      buildingNumber.length > 32 ||
      postalCode.length > 16
    ) {
      this.errorMessage.set(
        'Jedno z pól przekracza dozwoloną długość.',
      );
      return;
    }

    this.saving.set(true);

    this.locationService
      .create(restaurantId, {
        name,
        city,
        street,
        buildingNumber,
        postalCode,
      })
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.router.navigate(this.listLink(), {
            state: {
              successMessage: `Lokal „${name}” został utworzony.`,
            },
          });
        },
        error: (error) => {
          console.error('Location create error:', error);

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność wprowadzonych danych.',
            );
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do tworzenia lokali.',
            );
            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Lokal o podanych danych już istnieje.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się utworzyć lokalu.',
          );
        },
      });
  }
}