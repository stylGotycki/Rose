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
import {
  finalize,
  forkJoin,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { MenuReadDto } from '../../../shared/models/menu.model';
import { Menu } from '../menu';

@Component({
  selector: 'app-category-create-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './category-create-page.html',
  styleUrl: './category-create-page.css',
})
export class CategoryCreatePage implements OnInit {
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
  displayOrder = 0;

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

    this.loadData(menuId);
  }

  detailsLink(): string[] {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const menu = this.menu();

    if (!restaurantId || !menu) {
      return ['/app/restaurants'];
    }

    return [
      '/app/restaurants',
      restaurantId,
      'panel',
      'menu',
      menu.id,
      'details',
    ];
  }

  submit(): void {
    this.errorMessage.set(null);

    const menu = this.menu();

    if (!menu) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranego menu.',
      );

      return;
    }

    if (menu.status === 'ARCHIVED') {
      this.errorMessage.set(
        'Nie można dodawać kategorii do archiwalnego menu.',
      );

      return;
    }

    const name = this.name.trim();

    if (!name) {
      this.errorMessage.set(
        'Nazwa kategorii jest wymagana.',
      );

      return;
    }

    if (name.length > 128) {
      this.errorMessage.set(
        'Nazwa kategorii może mieć maksymalnie 128 znaków.',
      );

      return;
    }

    if (
      !Number.isInteger(this.displayOrder) ||
      this.displayOrder < 0
    ) {
      this.errorMessage.set(
        'Kolejność musi być nieujemną liczbą całkowitą.',
      );

      return;
    }

    this.saving.set(true);

    this.menuService
      .createCategory(menu.id, {
        name,
        displayOrder:
          this.displayOrder,
      })
      .pipe(
        finalize(() =>
          this.saving.set(false),
        ),
      )
      .subscribe({
        next: () => {
          this.router.navigate(
            this.detailsLink(),
            {
              state: {
                successMessage:
                  'Kategoria została utworzona.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Category create error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź nazwę i kolejność kategorii.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do tworzenia kategorii.',
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
              'Kategoria o takiej nazwie lub kolejności już istnieje.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się utworzyć kategorii.',
          );
        },
      });
  }

  private loadData(
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

    forkJoin({
      menu:
        this.menuService.getById(
          menuId,
        ),

      categories:
        this.menuService.getCategories(
          menuId,
        ),
    })
      .pipe(
        finalize(() =>
          this.loading.set(false),
        ),
      )
      .subscribe({
        next: ({
          menu,
          categories,
        }) => {
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

          const existing =
            categories.items ?? [];

          this.displayOrder =
            existing.length === 0
              ? 0
              : Math.max(
                  ...existing.map(
                    (category) =>
                      category.displayOrder,
                  ),
                ) + 1;
        },
        error: (error) => {
          console.error(
            'Category create data error:',
            error,
          );

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