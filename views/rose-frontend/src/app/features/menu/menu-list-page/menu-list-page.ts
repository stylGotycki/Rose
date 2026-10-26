import { DatePipe } from '@angular/common';
import {
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  Observable,
  finalize,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import {
  MenuReadDto,
  MenuStatus,
} from '../../../shared/models/menu.model';
import { Menu } from '../menu';

type MenuAction =
  | 'activate'
  | 'archive';

interface PendingMenuAction {
  type: MenuAction;
  menu: MenuReadDto;
}

@Component({
  selector: 'app-menu-list-page',
  imports: [
    RouterLink,
    DatePipe,
  ],
  templateUrl: './menu-list-page.html',
  styleUrl: './menu-list-page.css',
})
export class MenuListPage implements OnInit {
  private readonly menuService = inject(Menu);

  readonly restaurantContext = inject(RestaurantContext);

  readonly menus = signal<MenuReadDto[]>([]);
  readonly loading = signal(true);

  readonly processingMenuId =
    signal<string | null>(null);

  readonly errorMessage =
    signal<string | null>(null);

  readonly successMessage =
    signal<string | null>(null);

  readonly pendingAction =
    signal<PendingMenuAction | null>(null);

  readonly activeMenu = computed(
    () =>
      this.menus().find(
        (menu) => menu.status === 'ACTIVE',
      ) ?? null,
  );

  readonly draftMenus = computed(() =>
    this.menus()
      .filter(
        (menu) => menu.status === 'DRAFT',
      )
      .sort(
        (first, second) =>
          new Date(second.updatedAt).getTime() -
          new Date(first.updatedAt).getTime(),
      ),
  );

  readonly archivedMenus = computed(() =>
    this.menus()
      .filter(
        (menu) => menu.status === 'ARCHIVED',
      )
      .sort(
        (first, second) =>
          new Date(second.updatedAt).getTime() -
          new Date(first.updatedAt).getTime(),
      ),
  );

  ngOnInit(): void {
    const message =
      history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

    this.loadMenus();
  }

  loadMenus(
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

    this.menuService
      .getAllByRestaurant(restaurantId)
      .pipe(
        finalize(() =>
          this.loading.set(false),
        ),
      )
      .subscribe({
        next: (response) => {
          this.menus.set(
            response.items ?? [],
          );
        },
        error: (error) => {
          console.error(
            'Menu loading error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania menu restauracji.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać menu restauracji.',
          );
        },
      });
  }

  createLink(): string[] {
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
      'create',
    ];
  }

  detailsLink(
    menuId: string,
  ): string[] {
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
      menuId,
      'details',
    ];
  }

  editLink(
    menuId: string,
  ): string[] {
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
      menuId,
      'edit',
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

  openAction(
    type: MenuAction,
    menu: MenuReadDto,
  ): void {
    this.pendingAction.set({
      type,
      menu,
    });
  }

  closeAction(): void {
    if (this.processingMenuId()) {
      return;
    }

    this.pendingAction.set(null);
  }

  confirmAction(): void {
    const action =
      this.pendingAction();

    if (!action) {
      return;
    }

    this.processingMenuId.set(
      action.menu.id,
    );

    this.errorMessage.set(null);

    const request$: Observable<unknown> =
      action.type === 'activate'
        ? this.menuService.activate(
            action.menu.id,
          )
        : this.menuService.archive(
            action.menu.id,
          );

    request$
      .pipe(
        finalize(() =>
          this.processingMenuId.set(null),
        ),
      )
      .subscribe({
        next: () => {
          this.pendingAction.set(null);

          this.showSuccess(
            action.type === 'activate'
              ? `Menu „${action.menu.name}” zostało aktywowane.`
              : `Menu „${action.menu.name}” zostało zarchiwizowane.`,
          );

          this.loadMenus(false);
        },
        error: (error) => {
          console.error(
            'Menu action error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              action.type === 'activate'
                ? 'Menu nie może zostać aktywowane w obecnym stanie.'
                : 'Menu nie może zostać zarchiwizowane w obecnym stanie.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do wykonania tej operacji.',
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
              action.type === 'activate'
                ? 'Menu nie spełnia warunków wymaganych do aktywacji.'
                : 'Menu jest wykorzystywane i nie może zostać zarchiwizowane.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się wykonać operacji.',
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
        this.successMessage() === message
      ) {
        this.successMessage.set(null);
      }
    }, 4000);
  }
}