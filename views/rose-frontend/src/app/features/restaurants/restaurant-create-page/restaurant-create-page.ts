import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';

import { Restaurant } from '../restaurant';

@Component({
  selector: 'app-restaurant-create-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './restaurant-create-page.html',
  styleUrl: './restaurant-create-page.css',
})
export class RestaurantCreatePage {
  private readonly restaurantService = inject(Restaurant);
  private readonly router = inject(Router);

  name = '';
  description = '';
  taxId = '';

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly loadingMessage = signal<string | null>(null);

  submit(): void {
    this.errorMessage.set(null);
    this.loadingMessage.set(null);

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

    this.loading.set(true);
    this.loadingMessage.set('Tworzenie restauracji...');

    this.restaurantService
      .create({
        name: normalizedName,
        description: normalizedDescription || null,
        taxId: normalizedTaxId || null,
      })
      .pipe(
        switchMap(() => {
          this.loadingMessage.set(
            'Przygotowywanie dostępu do restauracji...',
          );

          return this.restaurantService.waitUntilVisible(
            normalizedName,
            normalizedTaxId || null,
          );
        }),
      )
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.loadingMessage.set(null);

          this.router.navigate(['/app/restaurants'], {
            state: {
              successMessage:
                'Restauracja została utworzona.',
            },
          });
        },
        error: (error) => {
          console.error('Restaurant create error:', error);

          this.loading.set(false);
          this.loadingMessage.set(null);

          if (error.status === 409) {
            this.errorMessage.set(
              'Restauracja o podanych danych już istnieje.',
            );
            return;
          }

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność wprowadzonych danych.',
            );
            return;
          }

          this.errorMessage.set(
            'Restauracja została utworzona, ale jej przygotowanie trwa dłużej niż zwykle. Wróć do listy restauracji za chwilę.',
          );
        },
      });
  }
}