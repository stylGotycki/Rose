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
import {
  MenuReadDto,
  MenuStatus,
} from '../../../shared/models/menu.model';
import { Menu } from '../menu';

@Component({
  selector: 'app-menu-edit-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './menu-edit-page.html',
  styleUrl: './menu-edit-page.css',
})
export class MenuEditPage implements OnInit {
  private readonly menuService = inject(Menu);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly menu =
    signal<MenuReadDto | null>(null);

  readonly loading = signal(true);
  readonly saving = signal(false);

  readonly errorMessage =
    signal<string | null>(null);

  name = '';
  description = '';

  ngOnInit(): void {
    const menuId =
      this.route.snapshot.paramMap.get(
        'menuId',
      );

    if (!menuId) {
      this.loading.set(false);

      this.errorMessage.set(
        'Brak identyfikatora menu.',
      );

      return;
    }

    this.loadMenu(menuId);
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
      'menu',
    ];
  }

  statusLabel(
    status: MenuStatus,
  ): string {
    const labels: Record<
      MenuStatus,
      string
    > = {
      DRAFT: 'Wersja robocza',
      ACTIVE: 'Aktywne',
      ARCHIVED: 'Archiwalne',
    };

    return labels[status];
  }

  submit(): void {
    this.errorMessage.set(null);

    const menu = this.menu();

    if (!menu) {
      this.errorMessage.set(
        'Nie udało się ustalić edytowanego menu.',
      );

      return;
    }

    if (menu.status === 'ARCHIVED') {
      this.errorMessage.set(
        'Archiwalnego menu nie można edytować.',
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
      .update(menu.id, {
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
                  'Menu zostało zaktualizowane.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Menu update error:',
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
              'Nie masz uprawnień do edycji menu.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego menu.',
            );

            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Menu o takiej nazwie już istnieje albo nie może zostać zmienione.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się zaktualizować menu.',
          );
        },
      });
  }

  private loadMenu(
    menuId: string,
  ): void {
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

    this.menuService
      .getById(menuId)
      .pipe(
        finalize(() =>
          this.loading.set(false),
        ),
      )
      .subscribe({
        next: (menu) => {
          if (
            menu.restaurantId !==
            restaurantId
          ) {
            this.errorMessage.set(
              'Wybrane menu nie należy do tej restauracji.',
            );

            return;
          }

          this.menu.set(menu);

          this.name = menu.name;
          this.description =
            menu.description ?? '';
        },
        error: (error) => {
          console.error(
            'Menu details error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do wybranego menu.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego menu.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać danych menu.',
          );
        },
      });
  }
}