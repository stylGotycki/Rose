import {
  DatePipe,
  DecimalPipe,
} from '@angular/common';
import {
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import {
  ActivatedRoute,
  RouterLink,
} from '@angular/router';
import {
  Observable,
  catchError,
  finalize,
  forkJoin,
  of,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { CollectionRestDto } from '../../../shared/models/collection-rest.model';
import {
  MenuCategoryReadDto,
  MenuCategoryStatus,
  MenuItemReadDto,
  MenuItemStatus,
  MenuReadDto,
  MenuStatus,
} from '../../../shared/models/menu.model';
import { Menu } from '../menu';

@Component({
  selector: 'app-menu-details-page',
  imports: [
    RouterLink,
    DatePipe,
    DecimalPipe,
  ],
  templateUrl: './menu-details-page.html',
  styleUrl: './menu-details-page.css',
})
export class MenuDetailsPage implements OnInit {
  private readonly menuService = inject(Menu);
  private readonly route = inject(ActivatedRoute);

  readonly restaurantContext = inject(RestaurantContext);

  readonly menu =
    signal<MenuReadDto | null>(null);

  readonly categories =
    signal<MenuCategoryReadDto[]>([]);

  readonly itemsByCategory =
    signal<Record<string, MenuItemReadDto[]>>({});

  readonly loading = signal(true);

  readonly processingEntityId =
    signal<string | null>(null);

  readonly errorMessage =
    signal<string | null>(null);

  readonly successMessage =
    signal<string | null>(null);

  readonly categoryToArchive =
    signal<MenuCategoryReadDto | null>(null);

  readonly itemToArchive =
    signal<MenuItemReadDto | null>(null);

  readonly activeCategories = computed(() =>
    this.categories()
      .filter(
        (category) =>
          category.status === 'ACTIVE',
      )
      .sort(
        (first, second) =>
          first.displayOrder -
            second.displayOrder ||
          first.name.localeCompare(
            second.name,
            'pl',
          ),
      ),
  );

  readonly archivedCategories = computed(() =>
    this.categories()
      .filter(
        (category) =>
          category.status === 'ARCHIVED',
      )
      .sort(
        (first, second) =>
          first.displayOrder -
            second.displayOrder ||
          first.name.localeCompare(
            second.name,
            'pl',
          ),
      ),
  );

  readonly allItems = computed(() =>
    Object.values(
      this.itemsByCategory(),
    ).flat(),
  );

  readonly activeItemCount = computed(() =>
    this.allItems().filter(
      (item) =>
        item.status === 'ACTIVE',
    ).length,
  );

  readonly archivedItemCount = computed(() =>
    this.allItems().filter(
      (item) =>
        item.status === 'ARCHIVED',
    ).length,
  );

  readonly availableItemCount = computed(() =>
    this.allItems().filter(
      (item) =>
        item.status === 'ACTIVE' &&
        item.available,
    ).length,
  );

  readonly canManageMenu = computed(
    () =>
      this.menu()?.status !== 'ARCHIVED',
  );

  ngOnInit(): void {
    const message =
      history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

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

  editMenuLink(): string[] {
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
      'edit',
    ];
  }

  createCategoryLink(): string[] {
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
      'categories',
      'create',
    ];
  }

  editCategoryLink(
    categoryId: string,
  ): string[] {
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
      'categories',
      categoryId,
      'edit',
    ];
  }

  createItemLink(
    categoryId: string,
  ): string[] {
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
      'categories',
      categoryId,
      'items',
      'create',
    ];
  }

  editItemLink(
    categoryId: string,
    itemId: string,
  ): string[] {
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
      'categories',
      categoryId,
      'items',
      itemId,
      'edit',
    ];
  }

  menuStatusLabel(
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

  categoryStatusLabel(
    status: MenuCategoryStatus,
  ): string {
    const labels: Record<
      MenuCategoryStatus,
      string
    > = {
      ACTIVE: 'Aktywna',
      ARCHIVED: 'Archiwalna',
    };

    return labels[status];
  }

  itemStatusLabel(
    status: MenuItemStatus,
  ): string {
    const labels: Record<
      MenuItemStatus,
      string
    > = {
      ACTIVE: 'Aktywna',
      ARCHIVED: 'Archiwalna',
    };

    return labels[status];
  }

  activeItemsForCategory(
    categoryId: string,
  ): MenuItemReadDto[] {
    return (
      this.itemsByCategory()[categoryId] ?? []
    )
      .filter(
        (item) =>
          item.status === 'ACTIVE',
      )
      .sort(
        (first, second) => {
          if (
            first.available !==
            second.available
          ) {
            return first.available
              ? -1
              : 1;
          }

          return first.name.localeCompare(
            second.name,
            'pl',
          );
        },
      );
  }

  archivedItemsForCategory(
    categoryId: string,
  ): MenuItemReadDto[] {
    return (
      this.itemsByCategory()[categoryId] ?? []
    )
      .filter(
        (item) =>
          item.status === 'ARCHIVED',
      )
      .sort(
        (first, second) =>
          first.name.localeCompare(
            second.name,
            'pl',
          ),
      );
  }

  allItemsForCategory(
    categoryId: string,
  ): MenuItemReadDto[] {
    return [
      ...this.activeItemsForCategory(
        categoryId,
      ),
      ...this.archivedItemsForCategory(
        categoryId,
      ),
    ];
  }

  canManageCategory(
    category: MenuCategoryReadDto,
  ): boolean {
    return (
      this.canManageMenu() &&
      category.status === 'ACTIVE'
    );
  }

  openArchiveCategory(
    category: MenuCategoryReadDto,
  ): void {
    if (!this.canManageCategory(category)) {
      return;
    }

    this.categoryToArchive.set(
      category,
    );
  }

  closeArchiveCategory(): void {
    if (this.processingEntityId()) {
      return;
    }

    this.categoryToArchive.set(null);
  }

  confirmArchiveCategory(): void {
    const category =
      this.categoryToArchive();

    const menu = this.menu();

    if (!category || !menu) {
      return;
    }

    this.processingEntityId.set(
      category.id,
    );

    this.errorMessage.set(null);

    this.menuService
      .archiveCategory(category.id)
      .pipe(
        finalize(() =>
          this.processingEntityId.set(null),
        ),
      )
      .subscribe({
        next: () => {
          this.categoryToArchive.set(null);

          this.showSuccess(
            `Kategoria „${category.name}” została zarchiwizowana.`,
          );

          this.loadData(
            menu.id,
            false,
          );
        },
        error: (error) => {
          console.error(
            'Category archive error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do archiwizowania kategorii.',
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
              'Najpierw zarchiwizuj aktywne pozycje znajdujące się w tej kategorii.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się zarchiwizować kategorii.',
          );
        },
      });
  }

  openArchiveItem(
    item: MenuItemReadDto,
  ): void {
    if (!this.canManageMenu()) {
      return;
    }

    this.itemToArchive.set(item);
  }

  closeArchiveItem(): void {
    if (this.processingEntityId()) {
      return;
    }

    this.itemToArchive.set(null);
  }

  confirmArchiveItem(): void {
    const item =
      this.itemToArchive();

    const menu = this.menu();

    if (!item || !menu) {
      return;
    }

    this.processingEntityId.set(
      item.id,
    );

    this.errorMessage.set(null);

    this.menuService
      .archiveItem(item.id)
      .pipe(
        finalize(() =>
          this.processingEntityId.set(null),
        ),
      )
      .subscribe({
        next: () => {
          this.itemToArchive.set(null);

          this.showSuccess(
            `Pozycja „${item.name}” została zarchiwizowana.`,
          );

          this.loadData(
            menu.id,
            false,
          );
        },
        error: (error) => {
          console.error(
            'Menu item archive error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do archiwizowania pozycji menu.',
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
              'Pozycja jest obecnie wykorzystywana i nie może zostać zarchiwizowana.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się zarchiwizować pozycji.',
          );
        },
      });
  }

  private loadData(
    menuId: string,
    showLoading = true,
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

    if (showLoading) {
      this.loading.set(true);
    }

    this.errorMessage.set(null);

    forkJoin({
      menu:
        this.menuService.getById(
          menuId,
        ),

      categories:
        this.menuService.getCategories(
          menuId,
        ),
    }).subscribe({
      next: ({
        menu,
        categories,
      }) => {
        if (
          menu.restaurantId !==
          restaurantId
        ) {
          this.loading.set(false);
          this.menu.set(null);

          this.errorMessage.set(
            'Wybrane menu nie należy do tej restauracji.',
          );

          return;
        }

        const categoryList =
          categories.items ?? [];

        this.menu.set(menu);
        this.categories.set(
          categoryList,
        );

        this.loadItems(categoryList);
      },
      error: (error) => {
        this.loading.set(false);

        console.error(
          'Menu details loading error:',
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
          'Nie udało się pobrać zawartości menu.',
        );
      },
    });
  }

  private loadItems(
    categories: MenuCategoryReadDto[],
  ): void {
    if (categories.length === 0) {
      this.itemsByCategory.set({});
      this.loading.set(false);

      return;
    }

    const requests: Record<
      string,
      Observable<
        CollectionRestDto<MenuItemReadDto>
      >
    > = {};

    for (const category of categories) {
      requests[category.id] =
        this.menuService
          .getItems(category.id)
          .pipe(
            catchError((error) => {
              console.warn(
                `Nie udało się pobrać pozycji kategorii ${category.id}:`,
                error,
              );

              return of({
                items: [],
              });
            }),
          );
    }

    forkJoin(requests)
      .pipe(
        finalize(() =>
          this.loading.set(false),
        ),
      )
      .subscribe({
        next: (responses) => {
          const result: Record<
            string,
            MenuItemReadDto[]
          > = {};

          for (
            const [
              categoryId,
              response,
            ] of Object.entries(responses)
          ) {
            result[categoryId] =
              response.items ?? [];
          }

          this.itemsByCategory.set(
            result,
          );
        },
        error: (error) => {
          console.error(
            'Menu items loading error:',
            error,
          );

          this.errorMessage.set(
            'Nie udało się pobrać pozycji menu.',
          );
        },
      });
  }

  private showSuccess(
    message: string,
  ): void {
    this.successMessage.set(message);

    window.setTimeout(() => {
      if (
        this.successMessage() ===
        message
      ) {
        this.successMessage.set(null);
      }
    }, 4000);
  }
}