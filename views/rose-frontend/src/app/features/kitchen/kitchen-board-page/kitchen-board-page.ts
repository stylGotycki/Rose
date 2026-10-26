import {
  DatePipe,
} from '@angular/common';
import {
  Component,
  OnDestroy,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import {
  KitchenItemStatus,
  KitchenOrderItemReadDto,
} from '../../../shared/models/kitchen.model';
import { Kitchen } from '../kitchen';

interface KitchenOrderGroup {
  orderId: string;
  items: KitchenOrderItemReadDto[];
  oldestCreatedAt: string;
}

@Component({
  selector: 'app-kitchen-board-page',
  imports: [
    DatePipe,
  ],
  templateUrl: './kitchen-board-page.html',
  styleUrl: './kitchen-board-page.css',
})
export class KitchenBoardPage
  implements OnInit, OnDestroy {
  private readonly kitchenService = inject(Kitchen);

  readonly restaurantContext = inject(RestaurantContext);

  readonly items =
    signal<KitchenOrderItemReadDto[]>([]);

  readonly loading = signal(true);
  readonly refreshing = signal(false);

  readonly processingItemId =
    signal<string | null>(null);

  readonly errorMessage =
    signal<string | null>(null);

  readonly successMessage =
    signal<string | null>(null);

  private refreshInterval:
    ReturnType<typeof setInterval> | null = null;

  readonly pendingItems = computed(() =>
    this.items()
      .filter(
        (item) =>
          item.status === 'PENDING',
      )
      .sort(
        (first, second) =>
          new Date(first.createdAt).getTime() -
          new Date(second.createdAt).getTime(),
      ),
  );

  readonly preparationItems = computed(() =>
    this.items()
      .filter(
        (item) =>
          item.status === 'IN_PREPARATION',
      )
      .sort(
        (first, second) =>
          new Date(first.updatedAt).getTime() -
          new Date(second.updatedAt).getTime(),
      ),
  );

  readonly readyItems = computed(() =>
    this.items()
      .filter(
        (item) =>
          item.status === 'READY',
      )
      .sort(
        (first, second) =>
          new Date(first.updatedAt).getTime() -
          new Date(second.updatedAt).getTime(),
      ),
  );

  readonly pendingGroups = computed(() =>
    this.groupByOrder(
      this.pendingItems(),
    ),
  );

  readonly preparationGroups = computed(() =>
    this.groupByOrder(
      this.preparationItems(),
    ),
  );

  readonly readyGroups = computed(() =>
    this.groupByOrder(
      this.readyItems(),
    ),
  );

  readonly activeOrderCount = computed(
    () =>
      new Set(
        this.items().map(
          (item) => item.orderId,
        ),
      ).size,
  );

  readonly totalQuantity = computed(() =>
    this.items().reduce(
      (sum, item) =>
        sum + item.quantity,
      0,
    ),
  );

  ngOnInit(): void {
    this.loadItems();

    this.refreshInterval = setInterval(
      () => {
        if (!this.processingItemId()) {
          this.loadItems(false);
        }
      },
      15_000,
    );
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(
        this.refreshInterval,
      );

      this.refreshInterval = null;
    }
  }

  loadItems(
    showInitialLoading = true,
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

    if (showInitialLoading) {
      this.loading.set(true);
    } else {
      this.refreshing.set(true);
    }

    this.kitchenService
      .getActiveItems(restaurantId)
      .pipe(
        finalize(() => {
          this.loading.set(false);
          this.refreshing.set(false);
        }),
      )
      .subscribe({
        next: (items) => {
          this.items.set(
            items ?? [],
          );

          this.errorMessage.set(null);
        },
        error: (error) => {
          console.error(
            'Kitchen items loading error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania kuchni.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać pozycji kuchennych.',
          );
        },
      });
  }

  startPreparing(
    item: KitchenOrderItemReadDto,
  ): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (
      !restaurantId ||
      item.status !== 'PENDING'
    ) {
      return;
    }

    this.processingItemId.set(item.id);
    this.errorMessage.set(null);

    this.kitchenService
      .startPreparing(
        restaurantId,
        item.id,
      )
      .pipe(
        finalize(() =>
          this.processingItemId.set(null),
        ),
      )
      .subscribe({
        next: (updatedItem) => {
          this.replaceItem(updatedItem);

          this.showSuccess(
            `Rozpoczęto przygotowanie pozycji „${updatedItem.name}”.`,
          );
        },
        error: (error) => {
          console.error(
            'Kitchen start preparation error:',
            error,
          );

          this.handleActionError(
            error,
            'Nie udało się rozpocząć przygotowania pozycji.',
          );
        },
      });
  }

  markReady(
    item: KitchenOrderItemReadDto,
  ): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (
      !restaurantId ||
      item.status !== 'IN_PREPARATION'
    ) {
      return;
    }

    this.processingItemId.set(item.id);
    this.errorMessage.set(null);

    this.kitchenService
      .markReady(
        restaurantId,
        item.id,
      )
      .pipe(
        finalize(() =>
          this.processingItemId.set(null),
        ),
      )
      .subscribe({
        next: (updatedItem) => {
          this.replaceItem(updatedItem);

          this.showSuccess(
            `Pozycja „${updatedItem.name}” została oznaczona jako gotowa.`,
          );
        },
        error: (error) => {
          console.error(
            'Kitchen mark ready error:',
            error,
          );

          this.handleActionError(
            error,
            'Nie udało się oznaczyć pozycji jako gotowej.',
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

  statusLabel(
    status: KitchenItemStatus,
  ): string {
    const labels: Record<
      KitchenItemStatus,
      string
    > = {
      PENDING: 'Oczekuje',
      IN_PREPARATION: 'W przygotowaniu',
      READY: 'Gotowa',
    };

    return labels[status];
  }

  ageLabel(
    dateValue: string,
  ): string {
    const timestamp =
      new Date(dateValue).getTime();

    if (Number.isNaN(timestamp)) {
      return '';
    }

    const minutes = Math.max(
      0,
      Math.floor(
        (
          Date.now() -
          timestamp
        ) /
        60_000,
      ),
    );

    if (minutes < 1) {
      return 'przed chwilą';
    }

    if (minutes < 60) {
      return `${minutes} min temu`;
    }

    const hours =
      Math.floor(minutes / 60);

    const remainingMinutes =
      minutes % 60;

    if (remainingMinutes === 0) {
      return `${hours} godz. temu`;
    }

    return (
      `${hours} godz. ` +
      `${remainingMinutes} min temu`
    );
  }

  isWarning(
    item: KitchenOrderItemReadDto,
  ): boolean {
    const minutes =
      this.minutesSince(
        item.createdAt,
      );

    return (
      minutes >= 15 &&
      minutes < 30 &&
      item.status !== 'READY'
    );
  }

  isDelayed(
    item: KitchenOrderItemReadDto,
  ): boolean {
    return (
      this.minutesSince(
        item.createdAt,
      ) >= 30 &&
      item.status !== 'READY'
    );
  }

  private groupByOrder(
    items: KitchenOrderItemReadDto[],
  ): KitchenOrderGroup[] {
    const groups =
      new Map<
        string,
        KitchenOrderItemReadDto[]
      >();

    for (const item of items) {
      const current =
        groups.get(item.orderId) ?? [];

      current.push(item);

      groups.set(
        item.orderId,
        current,
      );
    }

    return Array.from(
      groups.entries(),
    )
      .map(
        ([
          orderId,
          groupedItems,
        ]) => ({
          orderId,
          items: groupedItems,
          oldestCreatedAt:
            groupedItems.reduce(
              (
                oldest,
                current,
              ) =>
                new Date(
                  current.createdAt,
                ).getTime() <
                new Date(
                  oldest.createdAt,
                ).getTime()
                  ? current
                  : oldest,
            ).createdAt,
        }),
      )
      .sort(
        (first, second) =>
          new Date(
            first.oldestCreatedAt,
          ).getTime() -
          new Date(
            second.oldestCreatedAt,
          ).getTime(),
      );
  }

  private replaceItem(
    updatedItem:
      KitchenOrderItemReadDto,
  ): void {
    this.items.update((items) => {
      const exists =
        items.some(
          (item) =>
            item.id === updatedItem.id,
        );

      if (!exists) {
        return [
          ...items,
          updatedItem,
        ];
      }

      return items.map(
        (item) =>
          item.id === updatedItem.id
            ? updatedItem
            : item,
      );
    });
  }

  private minutesSince(
    dateValue: string,
  ): number {
    const timestamp =
      new Date(dateValue).getTime();

    if (Number.isNaN(timestamp)) {
      return 0;
    }

    return Math.max(
      0,
      Math.floor(
        (
          Date.now() -
          timestamp
        ) /
        60_000,
      ),
    );
  }

  private handleActionError(
    error: {
      status?: number;
    },
    fallbackMessage: string,
  ): void {
    if (error.status === 400) {
      this.errorMessage.set(
        'Pozycja nie może zostać zmieniona w obecnym stanie.',
      );

      return;
    }

    if (error.status === 403) {
      this.errorMessage.set(
        'Nie masz uprawnień do obsługi kuchni.',
      );

      return;
    }

    if (error.status === 404) {
      this.errorMessage.set(
        'Nie znaleziono wybranej pozycji zamówienia.',
      );

      return;
    }

    if (error.status === 409) {
      this.errorMessage.set(
        'Status pozycji został już zmieniony. Odświeżono tablicę kuchenną.',
      );

      this.loadItems(false);

      return;
    }

    this.errorMessage.set(
      fallbackMessage,
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
    }, 3000);
  }
}