import {
  Component,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, switchMap } from 'rxjs';
import { DatePipe } from '@angular/common';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { Restaurant } from '../../restaurants/restaurant';

@Component({
  selector: 'app-panel-settings-page',
  imports: [FormsModule, DatePipe],
  templateUrl: './panel-settings-page.html',
  styleUrl: './panel-settings-page.css',
})
export class PanelSettingsPage implements OnInit {
  private readonly restaurantService = inject(Restaurant);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  name = '';
  description = '';
  taxId = '';

  readonly saving = signal(false);
  readonly archiving = signal(false);

  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly archiveConfirmationVisible = signal(false);

  ngOnInit(): void {
    const restaurant = this.restaurantContext.restaurant();

    if (!restaurant) {
      return;
    }

    this.name = restaurant.name;
    this.description = restaurant.description ?? '';
    this.taxId = restaurant.taxId ?? '';
  }

  submit(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );
      return;
    }

    const normalizedName = this.name.trim();
    const normalizedDescription = this.description.trim();
    const normalizedTaxId = this.taxId.trim();

    if (!normalizedName) {
      this.errorMessage.set(
        'Nazwa restauracji jest wymagana.',
      );
      return;
    }

    if (normalizedName.length > 128) {
      this.errorMessage.set(
        'Nazwa może mieć maksymalnie 128 znaków.',
      );
      return;
    }

    if (normalizedDescription.length > 1024) {
      this.errorMessage.set(
        'Opis może mieć maksymalnie 1024 znaki.',
      );
      return;
    }

    if (normalizedTaxId.length > 32) {
      this.errorMessage.set(
        'NIP / Tax ID może mieć maksymalnie 32 znaki.',
      );
      return;
    }

    this.saving.set(true);

    this.restaurantService
      .update(restaurantId, {
        name: normalizedName,
        description: normalizedDescription || null,
        taxId: normalizedTaxId || null,
      })
      .pipe(
        switchMap(() =>
          this.restaurantService.getById(restaurantId),
        ),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: (restaurant) => {
          this.restaurantContext.updateLocal(restaurant);

          this.name = restaurant.name;
          this.description = restaurant.description ?? '';
          this.taxId = restaurant.taxId ?? '';

          this.successMessage.set(
            'Dane restauracji zostały zapisane.',
          );

          window.setTimeout(() => {
            this.successMessage.set(null);
          }, 4000);
        },
        error: (error) => {
          console.error('Restaurant update error:', error);

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność wprowadzonych danych.',
            );
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do edycji restauracji.',
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
            'Nie udało się zapisać zmian.',
          );
        },
      });
  }

  showArchiveConfirmation(): void {
    this.archiveConfirmationVisible.set(true);
  }

  hideArchiveConfirmation(): void {
    if (this.archiving()) {
      return;
    }

    this.archiveConfirmationVisible.set(false);
  }

  archive(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );
      return;
    }

    this.archiving.set(true);
    this.errorMessage.set(null);

    this.restaurantService
      .archive(restaurantId)
      .pipe(
        finalize(() => this.archiving.set(false)),
      )
      .subscribe({
        next: () => {
          this.restaurantContext.clear();

          this.router.navigate(['/app/restaurants'], {
            state: {
              successMessage:
                'Restauracja została przeniesiona do archiwum.',
            },
          });
        },
        error: (error) => {
          console.error('Restaurant archive error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do archiwizacji restauracji.',
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
            'Nie udało się zarchiwizować restauracji.',
          );
        },
      });
  }
}