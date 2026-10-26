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
import { FormsModule } from '@angular/forms';
import {
  ActivatedRoute,
  Router,
  RouterLink,
} from '@angular/router';
import {
  Observable,
  catchError,
  finalize,
  forkJoin,
  of,
  switchMap,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { CollectionRestDto } from '../../../shared/models/collection-rest.model';
import {
  MenuCategoryReadDto,
  MenuItemReadDto,
  MenuReadDto,
} from '../../../shared/models/menu.model';
import {
  OrderItemReadDto,
  OrderReadDto,
} from '../../../shared/models/order.model';
import { Menu } from '../../menu/menu';
import { Order } from '../order';

@Component({
  selector: 'app-cashier-order-page',
  imports: [
    FormsModule,
    RouterLink,
    DatePipe,
    DecimalPipe,
  ],
  templateUrl: './cashier-order-page.html',
  styleUrl: './cashier-order-page.css',
})
export class CashierOrderPage implements OnInit {
  private readonly orderService = inject(Order);
  private readonly menuService = inject(Menu);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly activeMenu =
    signal<MenuReadDto | null>(null);

  readonly categories =
    signal<MenuCategoryReadDto[]>([]);

  readonly itemsByCategory =
    signal<Record<string, MenuItemReadDto[]>>({});

  readonly currentOrder =
    signal<OrderReadDto | null>(null);

  readonly loading = signal(true);
  readonly startingOrder = signal(false);
  readonly submittingOrder = signal(false);

  readonly processingItemId =
    signal<string | null>(null);

  readonly errorMessage =
    signal<string | null>(null);

  readonly successMessage =
    signal<string | null>(null);

  readonly itemNotes =
    signal<Record<string, string>>({});

  orderNote = '';

  readonly orderedCategories = computed(() =>
    [...this.categories()]
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

  readonly cartItems = computed(() =>
    [...(this.currentOrder()?.items ?? [])].sort(
      (first, second) =>
        first.createdAt.localeCompare(
          second.createdAt,
        ),
    ),
  );

  readonly totalQuantity = computed(() =>
    this.cartItems().reduce(
      (sum, item) =>
        sum + item.quantity,
      0,
    ),
  );

  readonly editableOrder = computed(
    () =>
      this.currentOrder()?.status === 'DRAFT',
  );

  ngOnInit(): void {
    this.loadPageData();
  }

  ordersLink(): string[] {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return ['/app/restaurants'];
    }

    return [
      '/app/restaurants',
      restaurantId,
      'panel',
      'orders',
    ];
  }

  availableItemsForCategory(
    categoryId: string,
  ): MenuItemReadDto[] {
    return (
      this.itemsByCategory()[categoryId] ?? []
    )
      .filter(
        (item) =>
          item.status === 'ACTIVE' &&
          item.available,
      )
      .sort(
        (first, second) =>
          first.name.localeCompare(
            second.name,
            'pl',
          ),
      );
  }

  startNewOrder(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );

      return;
    }

    const note = this.orderNote.trim();

    if (note.length > 512) {
      this.errorMessage.set(
        'Notatka może mieć maksymalnie 512 znaków.',
      );

      return;
    }

    this.startingOrder.set(true);
    this.errorMessage.set(null);

    this.orderService
      .create(restaurantId, {
        note: note || null,
      })
      .pipe(
        finalize(() =>
          this.startingOrder.set(false),
        ),
      )
      .subscribe({
        next: (order) => {
          this.currentOrder.set(order);
          this.initializeItemNotes(order);

          this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
              orderId: order.id,
            },
            queryParamsHandling: 'merge',
            replaceUrl: true,
          });

          this.showSuccess(
            `Rozpoczęto zamówienie #${this.shortId(order.id)}.`,
          );
        },
        error: (error) => {
          console.error(
            'Order creation error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do tworzenia zamówień.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się rozpocząć zamówienia.',
          );
        },
      });
  }

  addMenuItem(
    menuItem: MenuItemReadDto,
  ): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const order =
      this.currentOrder();

    if (
      !restaurantId ||
      !order ||
      order.status !== 'DRAFT'
    ) {
      return;
    }

    const existingItem =
      order.items.find(
        (item) =>
          item.menuItemId === menuItem.id,
      );

    this.processingItemId.set(menuItem.id);
    this.errorMessage.set(null);

    const request$: Observable<OrderReadDto> =
      existingItem
        ? this.orderService.updateItem(
            restaurantId,
            order.id,
            existingItem.id,
            {
              quantity:
                existingItem.quantity + 1,
              note:
                existingItem.note ?? null,
            },
          )
        : this.orderService.addItem(
            restaurantId,
            order.id,
            {
              menuItemId: menuItem.id,
              quantity: 1,
              note: null,
            },
          );

    request$
      .pipe(
        finalize(() =>
          this.processingItemId.set(null),
        ),
      )
      .subscribe({
        next: (updatedOrder) => {
          this.currentOrder.set(updatedOrder);
          this.initializeItemNotes(updatedOrder);
        },
        error: (error) => {
          console.error(
            'Add order item error:',
            error,
          );

          this.handleItemError(error);
        },
      });
  }

  increaseQuantity(
    item: OrderItemReadDto,
  ): void {
    this.updateQuantity(
      item,
      item.quantity + 1,
    );
  }

  decreaseQuantity(
    item: OrderItemReadDto,
  ): void {
    if (item.quantity <= 1) {
      this.removeItem(item);
      return;
    }

    this.updateQuantity(
      item,
      item.quantity - 1,
    );
  }

  removeItem(
    item: OrderItemReadDto,
  ): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const order =
      this.currentOrder();

    if (
      !restaurantId ||
      !order ||
      order.status !== 'DRAFT'
    ) {
      return;
    }

    this.processingItemId.set(item.id);
    this.errorMessage.set(null);

    this.orderService
      .removeItem(
        restaurantId,
        order.id,
        item.id,
      )
      .pipe(
        switchMap(() =>
          this.orderService.getById(
            restaurantId,
            order.id,
          ),
        ),
        finalize(() =>
          this.processingItemId.set(null),
        ),
      )
      .subscribe({
        next: (updatedOrder) => {
          this.currentOrder.set(updatedOrder);
          this.initializeItemNotes(updatedOrder);
        },
        error: (error) => {
          console.error(
            'Remove order item error:',
            error,
          );

          this.handleItemError(error);
        },
      });
  }

  setItemNote(
    itemId: string,
    value: string,
  ): void {
    this.itemNotes.update((notes) => ({
      ...notes,
      [itemId]: value,
    }));
  }

  itemNoteValue(
    itemId: string,
  ): string {
    return this.itemNotes()[itemId] ?? '';
  }

  saveItemNote(
    item: OrderItemReadDto,
  ): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const order =
      this.currentOrder();

    if (
      !restaurantId ||
      !order ||
      order.status !== 'DRAFT'
    ) {
      return;
    }

    const note =
      this.itemNoteValue(item.id).trim();

    if (note.length > 512) {
      this.errorMessage.set(
        'Notatka do pozycji może mieć maksymalnie 512 znaków.',
      );

      return;
    }

    this.processingItemId.set(item.id);
    this.errorMessage.set(null);

    this.orderService
      .updateItem(
        restaurantId,
        order.id,
        item.id,
        {
          quantity: item.quantity,
          note: note || null,
        },
      )
      .pipe(
        finalize(() =>
          this.processingItemId.set(null),
        ),
      )
      .subscribe({
        next: (updatedOrder) => {
          this.currentOrder.set(updatedOrder);
          this.initializeItemNotes(updatedOrder);

          this.showSuccess(
            'Notatka do pozycji została zapisana.',
          );
        },
        error: (error) => {
          console.error(
            'Update item note error:',
            error,
          );

          this.handleItemError(error);
        },
      });
  }

  submitCurrentOrder(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const order =
      this.currentOrder();

    if (
      !restaurantId ||
      !order ||
      order.status !== 'DRAFT'
    ) {
      return;
    }

    if (order.items.length === 0) {
      this.errorMessage.set(
        'Dodaj przynajmniej jedną pozycję do zamówienia.',
      );

      return;
    }

    this.submittingOrder.set(true);
    this.errorMessage.set(null);

    this.orderService
      .submit(
        restaurantId,
        order.id,
      )
      .pipe(
        finalize(() =>
          this.submittingOrder.set(false),
        ),
      )
      .subscribe({
        next: (submittedOrder) => {
          this.router.navigate(
            this.ordersLink(),
            {
              state: {
                successMessage:
                  `Zamówienie #${this.shortId(submittedOrder.id)} zostało wysłane do realizacji.`,
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Order submit error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Zamówienie nie może zostać wysłane. Sprawdź jego pozycje.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do wysyłania zamówień.',
            );

            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Jedna z pozycji jest niedostępna albo zamówienie zostało już wysłane.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się wysłać zamówienia.',
          );
        },
      });
  }

  shortId(
    id: string,
  ): string {
    return id
      .replaceAll('-', '')
      .slice(0, 8)
      .toUpperCase();
  }

  private updateQuantity(
    item: OrderItemReadDto,
    quantity: number,
  ): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const order =
      this.currentOrder();

    if (
      !restaurantId ||
      !order ||
      order.status !== 'DRAFT'
    ) {
      return;
    }

    this.processingItemId.set(item.id);
    this.errorMessage.set(null);

    this.orderService
      .updateItem(
        restaurantId,
        order.id,
        item.id,
        {
          quantity,
          note: item.note ?? null,
        },
      )
      .pipe(
        finalize(() =>
          this.processingItemId.set(null),
        ),
      )
      .subscribe({
        next: (updatedOrder) => {
          this.currentOrder.set(updatedOrder);
          this.initializeItemNotes(updatedOrder);
        },
        error: (error) => {
          console.error(
            'Update order item error:',
            error,
          );

          this.handleItemError(error);
        },
      });
  }

  private loadPageData(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.loading.set(false);

      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );

      return;
    }

    const orderId =
      this.route.snapshot.queryParamMap.get(
        'orderId',
      );

    const orderRequest$ = orderId
      ? this.orderService.getById(
          restaurantId,
          orderId,
        )
      : of(null);

    forkJoin({
      menus:
        this.menuService.getAllByRestaurant(
          restaurantId,
        ),

      order: orderRequest$,
    }).subscribe({
      next: ({
        menus,
        order,
      }) => {
        if (
          order &&
          order.restaurantId !== restaurantId
        ) {
          this.loading.set(false);

          this.errorMessage.set(
            'Wybrane zamówienie nie należy do tej restauracji.',
          );

          return;
        }

        this.currentOrder.set(order);

        if (order) {
          this.initializeItemNotes(order);
        }

        const activeMenu =
          (menus.items ?? []).find(
            (menu) =>
              menu.status === 'ACTIVE',
          ) ?? null;

        if (!activeMenu) {
          this.loading.set(false);

          this.errorMessage.set(
            'Restauracja nie posiada aktywnego menu. Aktywuj menu przed rozpoczęciem sprzedaży.',
          );

          return;
        }

        this.activeMenu.set(activeMenu);

        this.loadCategories(
          activeMenu.id,
        );
      },
      error: (error) => {
        this.loading.set(false);

        console.error(
          'Cashier initialization error:',
          error,
        );

        if (error.status === 404) {
          this.errorMessage.set(
            'Nie znaleziono wybranego zamówienia.',
          );

          return;
        }

        if (error.status === 403) {
          this.errorMessage.set(
            'Nie masz dostępu do widoku kasy.',
          );

          return;
        }

        this.errorMessage.set(
          'Nie udało się uruchomić widoku kasy.',
        );
      },
    });
  }

  private loadCategories(
    menuId: string,
  ): void {
    this.menuService
      .getCategories(menuId)
      .subscribe({
        next: (response) => {
          const categories =
            (response.items ?? []).filter(
              (category) =>
                category.status === 'ACTIVE',
            );

          this.categories.set(categories);
          this.loadMenuItems(categories);
        },
        error: (error) => {
          this.loading.set(false);

          console.error(
            'Cashier categories error:',
            error,
          );

          this.errorMessage.set(
            'Nie udało się pobrać kategorii menu.',
          );
        },
      });
  }

  private loadMenuItems(
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

          this.itemsByCategory.set(result);
        },
        error: (error) => {
          console.error(
            'Cashier menu items error:',
            error,
          );

          this.errorMessage.set(
            'Nie udało się pobrać pozycji menu.',
          );
        },
      });
  }

  private initializeItemNotes(
    order: OrderReadDto,
  ): void {
    const notes: Record<string, string> = {};

    for (const item of order.items) {
      notes[item.id] =
        item.note ?? '';
    }

    this.itemNotes.set(notes);
  }

  private handleItemError(
    error: {
      status?: number;
    },
  ): void {
    if (error.status === 400) {
      this.errorMessage.set(
        'Nie można zmienić pozycji zamówienia.',
      );

      return;
    }

    if (error.status === 403) {
      this.errorMessage.set(
        'Nie masz uprawnień do edycji zamówienia.',
      );

      return;
    }

    if (error.status === 404) {
      this.errorMessage.set(
        'Nie znaleziono zamówienia lub pozycji.',
      );

      return;
    }

    if (error.status === 409) {
      this.errorMessage.set(
        'Zamówienie zostało już wysłane albo pozycja nie jest dostępna.',
      );

      return;
    }

    this.errorMessage.set(
      'Nie udało się zmienić zamówienia.',
    );
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
    }, 3500);
  }
}