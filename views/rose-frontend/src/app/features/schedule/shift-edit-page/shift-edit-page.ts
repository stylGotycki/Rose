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
import { EmployeeReadDto } from '../../../shared/models/employee.model';
import {
  ScheduleShiftReadDto,
  ShiftPosition,
} from '../../../shared/models/schedule.model';
import { Employee } from '../../employees/employee';
import { Schedule } from '../schedule';

interface PositionOption {
  value: ShiftPosition;
  label: string;
}

@Component({
  selector: 'app-shift-edit-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './shift-edit-page.html',
  styleUrl: './shift-edit-page.css',
})
export class ShiftEditPage implements OnInit {
  private readonly scheduleService = inject(Schedule);
  private readonly employeeService = inject(Employee);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly shift =
    signal<ScheduleShiftReadDto | null>(null);

  readonly employee =
    signal<EmployeeReadDto | null>(null);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);

  startDate = '';
  startTime = '';

  endDate = '';
  endTime = '';

  position: ShiftPosition = 'STAFF';
  note = '';

  returnWeek = '';

  readonly positions: PositionOption[] = [
    {
      value: 'MANAGER',
      label: 'Menedżer',
    },
    {
      value: 'CHEF',
      label: 'Kucharz',
    },
    {
      value: 'WAITER',
      label: 'Kelner',
    },
    {
      value: 'BARTENDER',
      label: 'Barman',
    },
    {
      value: 'CASHIER',
      label: 'Kasjer',
    },
    {
      value: 'CLEANER',
      label: 'Osoba sprzątająca',
    },
    {
      value: 'DELIVERY',
      label: 'Dostawca',
    },
    {
      value: 'STAFF',
      label: 'Pracownik',
    },
  ];

  ngOnInit(): void {
    this.applyReturnWeek();

    const shiftId =
      this.route.snapshot.paramMap.get(
        'shiftId',
      );

    if (!shiftId) {
      this.loading.set(false);

      this.errorMessage.set(
        'Brak identyfikatora zmiany.',
      );
      return;
    }

    this.loadShift(shiftId);
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
      'schedule',
    ];
  }

  listQueryParams(): Record<string, string> {
    return {
      week: this.returnWeek,
    };
  }

  ensureValidEnd(): void {
    const start = this.buildLocalDate(
      this.startDate,
      this.startTime,
    );

    const end = this.buildLocalDate(
      this.endDate,
      this.endTime,
    );

    if (!start) {
      return;
    }

    if (
      !end ||
      end.getTime() <= start.getTime()
    ) {
      const newEnd = new Date(
        start.getTime() +
          8 * 60 * 60 * 1000,
      );

      this.setEndFields(newEnd);
    }

    this.returnWeek = this.toDateInput(
      this.getStartOfWeek(start),
    );
  }

  submit(): void {
    this.errorMessage.set(null);

    const shift = this.shift();

    if (!shift) {
      this.errorMessage.set(
        'Nie udało się ustalić edytowanej zmiany.',
      );
      return;
    }

    if (shift.status !== 'PLANNED') {
      this.errorMessage.set(
        'Edytować można wyłącznie zaplanowane zmiany.',
      );
      return;
    }

    const startsAt = this.buildLocalDate(
      this.startDate,
      this.startTime,
    );

    const endsAt = this.buildLocalDate(
      this.endDate,
      this.endTime,
    );

    if (!startsAt || !endsAt) {
      this.errorMessage.set(
        'Podaj poprawną datę i godzinę początku oraz końca zmiany.',
      );
      return;
    }

    if (
      endsAt.getTime() <=
      startsAt.getTime()
    ) {
      this.errorMessage.set(
        'Koniec zmiany musi być późniejszy od jej początku.',
      );
      return;
    }

    const note = this.note.trim();

    if (note.length > 1000) {
      this.errorMessage.set(
        'Notatka może mieć maksymalnie 1000 znaków.',
      );
      return;
    }

    this.saving.set(true);

    this.scheduleService
      .update(shift.id, {
        startsAt: startsAt.toISOString(),
        endsAt: endsAt.toISOString(),
        position: this.position,
        note: note || null,
      })
      .pipe(
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          const week = this.toDateInput(
            this.getStartOfWeek(startsAt),
          );

          this.router.navigate(
            this.listLink(),
            {
              queryParams: {
                week,
              },
              state: {
                successMessage:
                  'Zmiana została zaktualizowana.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Shift update error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność terminu i danych zmiany.',
            );
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do edycji grafiku.',
            );
            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranej zmiany.',
            );
            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Wybrany termin koliduje z inną zmianą pracownika.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się zapisać zmian.',
          );
        },
      });
  }

  private applyReturnWeek(): void {
    const week =
      this.route.snapshot.queryParamMap.get(
        'week',
      );

    if (
      week &&
      /^\d{4}-\d{2}-\d{2}$/.test(week)
    ) {
      this.returnWeek = week;
      return;
    }

    this.returnWeek = this.toDateInput(
      this.getStartOfWeek(new Date()),
    );
  }

  private loadShift(shiftId: string): void {
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

    forkJoin({
      shift:
        this.scheduleService.getById(
          shiftId,
        ),

      employees:
        this.employeeService.getAllByRestaurant(
          restaurantId,
        ),
    })
      .pipe(
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: ({ shift, employees }) => {
          if (
            shift.restaurantId !==
            restaurantId
          ) {
            this.errorMessage.set(
              'Wybrana zmiana nie należy do tej restauracji.',
            );
            return;
          }

          if (shift.status !== 'PLANNED') {
            this.errorMessage.set(
              'Nie można edytować zakończonej lub anulowanej zmiany.',
            );
            return;
          }

          const employee =
            (employees.items ?? []).find(
              (item) =>
                item.id === shift.employeeId,
            );

          this.shift.set(shift);
          this.employee.set(
            employee ?? null,
          );

          const startsAt = new Date(
            shift.startsAt,
          );

          const endsAt = new Date(
            shift.endsAt,
          );

          this.setStartFields(startsAt);
          this.setEndFields(endsAt);

          this.returnWeek = this.toDateInput(
            this.getStartOfWeek(startsAt),
          );

          this.position = shift.position;
          this.note = shift.note ?? '';
        },
        error: (error) => {
          console.error(
            'Shift details error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do wybranej zmiany.',
            );
            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranej zmiany.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać danych zmiany.',
          );
        },
      });
  }

  private buildLocalDate(
    dateValue: string,
    timeValue: string,
  ): Date | null {
    if (!dateValue || !timeValue) {
      return null;
    }

    const value = new Date(
      `${dateValue}T${timeValue}:00`,
    );

    if (Number.isNaN(value.getTime())) {
      return null;
    }

    return value;
  }

  private setStartFields(date: Date): void {
    this.startDate = this.toDateInput(date);
    this.startTime = this.toTimeInput(date);
  }

  private setEndFields(date: Date): void {
    this.endDate = this.toDateInput(date);
    this.endTime = this.toTimeInput(date);
  }

  private getStartOfWeek(date: Date): Date {
    const result = new Date(date);
    const day = result.getDay();

    const difference =
      day === 0 ? -6 : 1 - day;

    result.setDate(
      result.getDate() + difference,
    );

    result.setHours(0, 0, 0, 0);

    return result;
  }

  private toDateInput(date: Date): string {
    const year = date.getFullYear();

    const month = String(
      date.getMonth() + 1,
    ).padStart(2, '0');

    const day = String(
      date.getDate(),
    ).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  private toTimeInput(date: Date): string {
    const hours = String(
      date.getHours(),
    ).padStart(2, '0');

    const minutes = String(
      date.getMinutes(),
    ).padStart(2, '0');

    return `${hours}:${minutes}`;
  }
}