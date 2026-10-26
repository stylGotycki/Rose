import {
  Component,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Router,
  RouterLink,
} from '@angular/router';
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { Menu } from '../menu';

@Component({
  selector: 'app-menu-create-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './menu-create-page.html',
  styleUrl: './menu-create-page.css',
})
export class MenuCreatePage {
  private readonly menuService = inject(Menu);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly saving = signal(false);

  readonly errorMessage =
    signal<string | null>(null);

  name = '';
  description = '';

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
      'menu',
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

    const description =
      this.description.trim();

    if (!name) {
      this.errorMessage.set(
        'Nazwa menu jest wymagana.',
      );

      return;
    }

    if (name.length > 128) {
      this.errorMessage.set(
        'Nazwa menu może mieć maksymalnie 128 znaków.',
      );

      return;
    }

    if (description.length > 1000) {
      this.errorMessage.set(
        'Opis menu może mieć maksymalnie 1000 znaków.',
      );

      return;
    }

    this.saving.set(true);

    this.menuService
      .create(restaurantId, {
        name,
        description:
          description || null,
      })
      .pipe(
        finalize(() =>
          this.saving.set(false),
        ),
      )
      .subscribe({
        next: () => {
          this.router.navigate(
            this.listLink(),
            {
              state: {
                successMessage:
                  'Menu zostało utworzone jako wersja robocza.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Menu create error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność danych menu.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do tworzenia menu.',
            );

            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Menu o takiej nazwie już istnieje.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się utworzyć menu.',
          );
        },
      });
  }
}