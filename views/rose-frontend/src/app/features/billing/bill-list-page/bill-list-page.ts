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
  catchError,
  finalize,
  forkJoin,
  of,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import {
  BillReadDto,
  BillStatus,
} from '../../../shared/models/billing.model';
import { OrderReadDto } from '../../../shared/models/order.model';
import { Order } from '../../orders/order';
import { Billing } from '../billing';

type BillViewFilter =
  | 'OPEN'
  | 'PAID'
  | 'ALL';

@Component({
  selector: 'app-bill-list-page',
  imports: [
    DatePipe,
    DecimalPipe,
    FormsModule,
  ],
  templateUrl: './bill-list-page.html',
  styleUrl: './bill-list-page.css',
})
export class BillListPage implements OnInit {
  private readonly billingService = inject(Billing);
  private readonly orderService = inject(Order);

  readonly restaurantContext = inject(RestaurantContext);

  readonly bills = signal<BillReadDto[]>([]);

  readonly loading = signal(true);
  readonly refreshing = signal(false);
  readonly detailsLoading = signal(false);

  readonly payingBillId =
    signal<string | null>(null);

  readonly errorMessage =
    signal<string | null>(null);

  readonly successMessage =
    signal<string | null>(null);

  readonly detailsErrorMessage =
    signal<string | null>(null);

  readonly selectedBill =
    signal<BillReadDto | null>(null);

  readonly selectedOrder =
    signal<OrderReadDto | null>(null);

  readonly billToPay =
    signal<BillReadDto | null>(null);

  readonly viewFilter =
    signal<BillViewFilter>('OPEN');

  readonly searchQuery =
    signal<string>('');

  readonly openBills = computed(() =>
    this.bills()
      .filter(
        (bill) =>
          bill.status === 'OPEN',
      )
      .sort(
        (first, second) =>
          new Date(first.createdAt).getTime() -
          new Date(second.createdAt).getTime(),
      ),
  );

  readonly paidBills = computed(() =>
    this.bills()
      .filter(
        (bill) =>
          bill.status === 'PAID',
      )
      .sort(
        (first, second) =>
          new Date(
            second.paidAt ??
              second.updatedAt,
          ).getTime() -
          new Date(
            first.paidAt ??
              first.updatedAt,
          ).getTime(),
      ),
  );

  readonly paidTodayCount = computed(() =>
    this.paidBills().filter(
      (bill) =>
        bill.paidAt &&
        this.isToday(bill.paidAt),
    ).length,
  );

  readonly displayedBills = computed(() => {
    const filter =
      this.viewFilter();

    const query =
      this.searchQuery()
        .trim()
        .toLowerCase()
        .replaceAll('-', '');

    const source =
      filter === 'OPEN'
        ? this.openBills()
        : filter === 'PAID'
          ? this.paidBills()
          : [...this.bills()].sort(
              (first, second) =>
                new Date(
                  second.createdAt,
                ).getTime() -
                new Date(
                  first.createdAt,
                ).getTime(),
            );

    if (!query) {
      return source;
    }

    return source.filter((bill) => {
      const billId =
        bill.id
          .toLowerCase()
          .replaceAll('-', '');

      const orderId =
        bill.orderId
          .toLowerCase()
          .replaceAll('-', '');

      return (
        billId.includes(query) ||
        orderId.includes(query)
      );
    });
  });

  readonly totalBillCount = computed(
    () => this.bills().length,
  );

  ngOnInit(): void {
    const message =
      history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

    this.loadBills();
  }

  loadBills(
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

    this.errorMessage.set(null);

    this.billingService
      .getAll(restaurantId)
      .pipe(
        finalize(() => {
          this.loading.set(false);
          this.refreshing.set(false);
        }),
      )
      .subscribe({
        next: (bills) => {
          this.bills.set(
            bills ?? [],
          );

          this.updateSelectedBill(
            bills ?? [],
          );
        },
        error: (error) => {
          console.error(
            'Bills loading error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania rachunków.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać rachunków.',
          );
        },
      });
  }

  setViewFilter(
    filter: BillViewFilter,
  ): void {
    this.viewFilter.set(filter);
  }

  setSearchQuery(
    value: string,
  ): void {
    this.searchQuery.set(value);
  }

  clearSearch(): void {
    this.searchQuery.set('');
  }

  openDetails(
    bill: BillReadDto,
  ): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return;
    }

    this.selectedBill.set(bill);
    this.selectedOrder.set(null);
    this.detailsErrorMessage.set(null);
    this.detailsLoading.set(true);

    forkJoin({
      bill:
        this.billingService.getById(
          restaurantId,
          bill.id,
        ),

      order:
        this.orderService
          .getById(
            restaurantId,
            bill.orderId,
          )
          .pipe(
            catchError((error) => {
              console.error(
                'Bill order details error:',
                error,
              );

              this.detailsErrorMessage.set(
                'Nie udało się pobrać szczegółów zamówienia. Dane rachunku pozostają dostępne.',
              );

              return of(null);
            }),
          ),
    })
      .pipe(
        finalize(() =>
          this.detailsLoading.set(false),
        ),
      )
      .subscribe({
        next: ({
          bill: refreshedBill,
          order,
        }) => {
          this.selectedBill.set(
            refreshedBill,
          );

          this.selectedOrder.set(
            order,
          );

          this.replaceBill(
            refreshedBill,
          );
        },
        error: (error) => {
          console.error(
            'Bill details error:',
            error,
          );

          this.detailsErrorMessage.set(
            error.status === 404
              ? 'Nie znaleziono wybranego rachunku.'
              : 'Nie udało się pobrać szczegółów rachunku.',
          );
        },
      });
  }

  closeDetails(): void {
    if (this.detailsLoading()) {
      return;
    }

    this.selectedBill.set(null);
    this.selectedOrder.set(null);
    this.detailsErrorMessage.set(null);
  }

  openPayConfirmation(
    bill: BillReadDto,
  ): void {
    if (bill.status !== 'OPEN') {
      return;
    }

    this.selectedBill.set(null);
    this.selectedOrder.set(null);
    this.detailsErrorMessage.set(null);

    this.billToPay.set(bill);
  }

  closePayConfirmation(): void {
    if (this.payingBillId()) {
      return;
    }

    this.billToPay.set(null);
  }

  confirmPayment(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    const bill =
      this.billToPay();

    if (
      !restaurantId ||
      !bill ||
      bill.status !== 'OPEN'
    ) {
      return;
    }

    this.payingBillId.set(
      bill.id,
    );

    this.errorMessage.set(null);

    this.billingService
      .pay(
        restaurantId,
        bill.id,
      )
      .pipe(
        finalize(() =>
          this.payingBillId.set(null),
        ),
      )
      .subscribe({
        next: (paidBill) => {
          this.replaceBill(paidBill);
          this.billToPay.set(null);

          this.showSuccess(
            `Rachunek #${this.shortId(paidBill.id)} został oznaczony jako opłacony.`,
          );
        },
        error: (error) => {
          console.error(
            'Bill payment error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Rachunek nie może zostać opłacony w obecnym stanie.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do obsługi płatności.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego rachunku.',
            );

            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Rachunek został już opłacony albo jego status został zmieniony.',
            );

            this.loadBills(false);

            return;
          }

          this.errorMessage.set(
            'Nie udało się oznaczyć rachunku jako opłaconego.',
          );
        },
      });
  }

  statusLabel(
    status: BillStatus,
  ): string {
    const labels: Record<
      BillStatus,
      string
    > = {
      OPEN: 'Do zapłaty',
      PAID: 'Opłacony',
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

  totalQuantity(
    order: OrderReadDto,
  ): number {
    return order.items.reduce(
      (sum, item) =>
        sum + item.quantity,
      0,
    );
  }

  private replaceBill(
    updatedBill: BillReadDto,
  ): void {
    this.bills.update((bills) => {
      const exists =
        bills.some(
          (bill) =>
            bill.id === updatedBill.id,
        );

      if (!exists) {
        return [
          updatedBill,
          ...bills,
        ];
      }

      return bills.map(
        (bill) =>
          bill.id === updatedBill.id
            ? updatedBill
            : bill,
      );
    });

    if (
      this.selectedBill()?.id ===
      updatedBill.id
    ) {
      this.selectedBill.set(
        updatedBill,
      );
    }
  }

  private updateSelectedBill(
    bills: BillReadDto[],
  ): void {
    const selected =
      this.selectedBill();

    if (!selected) {
      return;
    }

    const updated =
      bills.find(
        (bill) =>
          bill.id === selected.id,
      ) ?? null;

    this.selectedBill.set(updated);
  }

  private isToday(
    dateValue: string,
  ): boolean {
    const date =
      new Date(dateValue);

    if (
      Number.isNaN(
        date.getTime(),
      )
    ) {
      return false;
    }

    const today =
      new Date();

    return (
      date.getFullYear() ===
        today.getFullYear() &&
      date.getMonth() ===
        today.getMonth() &&
      date.getDate() ===
        today.getDate()
    );
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