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
  MenuItemReadDto,
  MenuReadDto,
} from '../../../shared/models/menu.model';
import { Menu } from '../menu';

@Component({
  selector: 'app-item-edit-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './item-edit-page.html',
  styleUrl: './item-edit-page.css',
})
export class ItemEditPage implements OnInit {
  private readonly menuService = inject(Menu);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly menu =
    signal<MenuReadDto | null>(null);

  readonly category =
    signal<MenuCategoryReadDto | null>(null);

  readonly item =
    signal<MenuItemReadDto | null>(null);

  readonly loading = signal(true);
  readonly saving = signal(false);

  readonly errorMessage =
    signal<string | null>(null);

  name = '';
  description = '';
  price = 0;
  currency = 'PLN';
  available = true;

  ngOnInit(): void {
    const menuId =
      this.route.snapshot.paramMap.get(
        'menuId',
      );

    const categoryId =
      this.route.snapshot.paramMap.get(
        'categoryId',
      );

    const itemId =
      this.route.snapshot.paramMap.get(
        'itemId',
      );

    if (
      !menuId ||
      !categoryId ||
      !itemId
    ) {
      this.loading.set(false);

      this.errorMessage.set(
        'Brak identyfikatora menu, kategorii lub pozycji.',
      );

      return;
    }

    this.loadData(
      menuId,
      categoryId,
      itemId,
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

  normalizeCurrency(): void {
    this.currency =
      this.currency
        .trim()
        .toUpperCase()
        .slice(0, 3);
  }

  submit(): void {
    this.errorMessage.set(null);

    const menu = this.menu();
    const category = this.category();
    const item = this.item();

    if (!menu || !category || !item) {
      this.errorMessage.set(
        'Nie udało się ustalić edytowanej pozycji.',
      );

      return;
    }

    if (
      menu.status === 'ARCHIVED' ||
      category.status === 'ARCHIVED' ||
      item.status === 'ARCHIVED'
    ) {
      this.errorMessage.set(
        'Archiwalnej pozycji nie można edytować.',
      );

      return;
    }

    const name = this.name.trim();

    const description =
      this.description.trim();

    const currency =
      this.currency
        .trim()
        .toUpperCase();

    if (!name) {
      this.errorMessage.set(
        'Nazwa pozycji jest wymagana.',
      );

      return;
    }

    if (name.length > 128) {
      this.errorMessage.set(
        'Nazwa pozycji może mieć maksymalnie 128 znaków.',
      );

      return;
    }

    if (description.length > 1000) {
      this.errorMessage.set(
        'Opis może mieć maksymalnie 1000 znaków.',
      );

      return;
    }

    if (
      !Number.isFinite(this.price) ||
      this.price < 0
    ) {
      this.errorMessage.set(
        'Cena musi być liczbą większą lub równą zero.',
      );

      return;
    }

    if (!/^[A-Z]{3}$/.test(currency)) {
      this.errorMessage.set(
        'Waluta musi być trzyliterowym kodem, na przykład PLN lub EUR.',
      );

      return;
    }

    this.saving.set(true);

    this.menuService
      .updateItem(item.id, {
        name,
        description:
          description || null,
        price: this.price,
        currency,
        available: this.available,
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
                  'Pozycja menu została zaktualizowana.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Menu item update error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź nazwę, cenę, walutę i dostępność pozycji.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do edycji pozycji menu.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranej pozycji.',
            );

            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Pozycja o takiej nazwie już istnieje lub nie może zostać zmieniona.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się zaktualizować pozycji menu.',
          );
        },
      });
  }

  private loadData(
    menuId: string,
    categoryId: string,
    itemId: string,
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

      item:
        this.menuService.getItemById(
          itemId,
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
          item,
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
            category.menuId !==
            menu.id
          ) {
            this.errorMessage.set(
              'Wybrana kategoria nie należy do tego menu.',
            );

            return;
          }

          if (
            item.categoryId !==
            category.id
          ) {
            this.errorMessage.set(
              'Wybrana pozycja nie należy do tej kategorii.',
            );

            return;
          }

          this.menu.set(menu);
          this.category.set(category);
          this.item.set(item);

          this.name = item.name;

          this.description =
            item.description ?? '';

          this.price = item.price;
          this.currency = item.currency;
          this.available = item.available;
        },
        error: (error) => {
          console.error(
            'Menu item details error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do wybranej pozycji.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranej pozycji.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać danych pozycji.',
          );
        },
      });
  }
}