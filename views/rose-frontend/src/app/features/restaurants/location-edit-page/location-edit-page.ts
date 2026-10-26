import {
  Component,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ActivatedRoute,
  Router,
  RouterLink,
} from '@angular/router';
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { Location } from '../location';

@Component({
  selector: 'app-location-edit-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './location-edit-page.html',
  styleUrl: './location-edit-page.css',
})
export class LocationEditPage implements OnInit {
  private readonly locationService = inject(Location);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly locationId = signal<string | null>(null);
  readonly loading = signal(true);
  readonly saving = signal(false);

  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  name = '';
  city = '';
  street = '';
  buildingNumber = '';
  postalCode = '';

  ngOnInit(): void {
    const locationId =
      this.route.snapshot.paramMap.get('locationId');

    if (!locationId) {
      this.errorMessage.set(
        'Brak identyfikatora lokalu.',
      );
      this.loading.set(false);
      return;
    }

    this.locationId.set(locationId);
    this.loadLocation(locationId);
  }

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
    this.successMessage.set(null);

    const locationId = this.locationId();

    if (!locationId) {
      this.errorMessage.set(
        'Nie udało się ustalić edytowanego lokalu.',
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
      .update(locationId, {
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
              successMessage: `Dane lokalu „${name}” zostały zapisane.`,
            },
          });
        },
        error: (error) => {
          console.error('Location update error:', error);

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność wprowadzonych danych.',
            );
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do edycji lokalu.',
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
            'Nie udało się zapisać zmian.',
          );
        },
      });
  }

  private loadLocation(locationId: string): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.locationService
      .getById(locationId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (location) => {
          const restaurantId =
            this.restaurantContext.restaurantId();

          if (
            restaurantId &&
            location.restaurantId !== restaurantId
          ) {
            this.errorMessage.set(
              'Wybrany lokal nie należy do tej restauracji.',
            );
            return;
          }

          this.name = location.name;
          this.city = location.city;
          this.street = location.street;
          this.buildingNumber = location.buildingNumber;
          this.postalCode = location.postalCode;
        },
        error: (error) => {
          console.error('Location details error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do wybranego lokalu.',
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
            'Nie udało się pobrać danych lokalu.',
          );
        },
      });
  }
}