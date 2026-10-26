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
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import {
  OrderItemStatus,
  OrderReadDto,
  OrderStatus,
} from '../../../shared/models/order.model';
import { Order } from '../order';

type OrderViewFilter =
  | 'ACTIVE'
  | 'COMPLETED'
  | 'ALL';

@Component({
  selector: 'app-order-list-page',
  imports: [
    RouterLink,
    DatePipe,
    DecimalPipe,
  ],
  templateUrl: './order-list-page.html',
  styleUrl: './order-list-page.css',
})
export class OrderListPage implements OnInit {
  private readonly orderService = inject(Order);

  readonly restaurantContext = inject(RestaurantContext);

  readonly orders = signal<OrderReadDto[]>([]);
  readonly loading = signal(true);

  readonly errorMessage =
    signal<string | null>(null);

  readonly successMessage =
    signal<string | null>(null);

  readonly selectedOrder =
    signal<OrderReadDto | null>(null);

  readonly orderToComplete =
    signal<OrderReadDto | null>(null);

  readonly completingOrderId =
    signal<string | null>(null);

  readonly viewFilter =
    signal<OrderViewFilter>('ACTIVE');

  readonly activeOrders = computed(() =>
    this.orders()
      .filter(
        (order) =>
          order.status !== 'COMPLETED',
      )
      .sort(
        (first, second) =>
          new Date(second.createdAt).getTime() -
          new Date(first.createdAt).getTime(),
      ),
  );

  readonly completedOrders = computed(() =>
    this.orders()
      .filter(
        (order) =>
          order.status === 'COMPLETED',
      )
      .sort(
        (first, second) =>
          new Date(second.updatedAt).getTime() -
          new Date(first.updatedAt).getTime(),
      ),
  );

  readonly displayedOrders = computed(() => {
    if (this.viewFilter() === 'ACTIVE') {
      return this.activeOrders();
    }

    if (this.viewFilter() === 'COMPLETED') {
      return this.completedOrders();
    }

    return [...this.orders()].sort(
      (first, second) =>
        new Date(second.createdAt).getTime() -
        new Date(first.createdAt).getTime(),
    );
  });

  readonly draftCount = computed(() =>
    this.orders().filter(
      (order) => order.status === 'DRAFT',
    ).length,
  );

  readonly submittedCount = computed(() =>
    this.orders().filter(
      (order) =>
        order.status === 'SUBMITTED',
    ).length,
  );

  readonly preparationCount = computed(() =>
    this.orders().filter(
      (order) =>
        order.status === 'IN_PREPARATION',
    ).length,
  );

  readonly readyCount = computed(() =>
    this.orders().filter(
      (order) => order.status === 'READY',
    ).length,
  );

  ngOnInit(): void {
    const message =
      history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

    this.loadOrders();
  }

  loadOrders(
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

    this.orderService
      .getAllByRestaurant(restaurantId)
      .pipe(
        finalize(() =>
          this.loading.set(false),
        ),
      )
      .subscribe({
        next: (orders) => {
          this.orders.set(orders ?? []);

          const selected =
            this.selectedOrder();

          if (selected) {
            const updated =
              (orders ?? []).find(
                (order) =>
                  order.id === selected.id,
              ) ?? null;

            this.selectedOrder.set(updated);
          }
        },
        error: (error) => {
          console.error(
            'Orders loading error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania zamówień.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać zamówień.',
          );
        },
      });
  }

  setViewFilter(
    filter: OrderViewFilter,
  ): void {
    this.viewFilter.set(filter);
  }

  cashierLink(): string[] {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return ['/app/restaurants'];
    }

    return [
      '/app/restaurants',
      restaurantId,
      'panel',
      'cashier',
    ];
  }

  resumeOrderQueryParams(
    orderId: string,
  ): Record<string, string> {
    return {
      orderId,
    };
  }

  openDetails(
    order: OrderReadDto,
  ): void {
    this.selectedOrder.set(order);
  }

  closeDetails(): void {
    this.selectedOrder.set(null);
  }

  openCompleteConfirmation(
    order: OrderReadDto,
  ): void {
    this.selectedOrder.set(null);
    this.orderToComplete.set(order);
  }

  closeCompleteConfirmation(): void {
    if (this.completingOrderId()) {
      return;
    }

    this.orderToComplete.set(null);
  }

  confirmComplete(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const order =
      this.orderToComplete();

    if (!restaurantId || !order) {
      return;
    }

    this.completingOrderId.set(order.id);
    this.errorMessage.set(null);

    this.orderService
      .complete(
        restaurantId,
        order.id,
      )
      .pipe(
        finalize(() =>
          this.completingOrderId.set(null),
        ),
      )
      .subscribe({
        next: (updatedOrder) => {
          this.orders.update((orders) =>
            orders.map((current) =>
              current.id === updatedOrder.id
                ? updatedOrder
                : current,
            ),
          );

          this.orderToComplete.set(null);

          this.showSuccess(
            `Zamówienie #${this.shortId(updatedOrder.id)} zostało zakończone.`,
          );
        },
        error: (error) => {
          console.error(
            'Order completion error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Zamówienie nie jest jeszcze gotowe do zakończenia.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do zakończenia zamówienia.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego zamówienia.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się zakończyć zamówienia.',
          );
        },
      });
  }

  orderStatusLabel(
    status: OrderStatus,
  ): string {
    const labels: Record<
      OrderStatus,
      string
    > = {
      DRAFT: 'Szkic',
      SUBMITTED: 'Przyjęte',
      IN_PREPARATION: 'W przygotowaniu',
      READY: 'Gotowe',
      COMPLETED: 'Zakończone',
    };

    return labels[status];
  }

  itemStatusLabel(
    status: OrderItemStatus,
  ): string {
    const labels: Record<
      OrderItemStatus,
      string
    > = {
      PENDING: 'Oczekuje',
      IN_PREPARATION: 'W przygotowaniu',
      READY: 'Gotowa',
      SERVED: 'Wydana',
    };

    return labels[status];
  }

  shortId(
    id: string,
  ): string {
    return id
      .replaceAll('-', '')
      .slice(0, 8)
      .toUpperCase();
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