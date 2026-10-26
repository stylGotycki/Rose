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
import {
  MenuCategoryReadDto,
  MenuReadDto,
} from '../../../shared/models/menu.model';
import { Menu } from '../menu';

@Component({
  selector: 'app-category-edit-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './category-edit-page.html',
  styleUrl: './category-edit-page.css',
})
export class CategoryEditPage implements OnInit {
  private readonly menuService = inject(Menu);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly menu =
    signal<MenuReadDto | null>(null);

  readonly category =
    signal<MenuCategoryReadDto | null>(null);

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

    const categoryId =
      this.route.snapshot.paramMap.get(
        'categoryId',
      );

    if (!menuId || !categoryId) {
      this.loading.set(false);

      this.errorMessage.set(
        'Brak identyfikatora menu lub kategorii.',
      );

      return;
    }

    this.loadData(
      menuId,
      categoryId,
    );
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
    const category = this.category();

    if (!menu || !category) {
      this.errorMessage.set(
        'Nie udało się ustalić edytowanej kategorii.',
      );

      return;
    }

    if (
      menu.status === 'ARCHIVED' ||
      category.status === 'ARCHIVED'
    ) {
      this.errorMessage.set(
        'Archiwalnej kategorii nie można edytować.',
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
      .updateCategory(category.id, {
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
                  'Kategoria została zaktualizowana.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Category update error:',
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
              'Nie masz uprawnień do edycji kategorii.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranej kategorii.',
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
            'Nie udało się zaktualizować kategorii.',
          );
        },
      });
  }

  private loadData(
    menuId: string,
    categoryId: string,
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

      category:
        this.menuService.getCategoryById(
          categoryId,
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
          category,
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

          if (
            category.menuId !== menu.id
          ) {
            this.errorMessage.set(
              'Wybrana kategoria nie należy do tego menu.',
            );

            return;
          }

          this.menu.set(menu);
          this.category.set(category);

          this.name = category.name;

          this.displayOrder =
            category.displayOrder;
        },
        error: (error) => {
          console.error(
            'Category details error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do wybranej kategorii.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranej kategorii.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać danych kategorii.',
          );
        },
      });
  }
}