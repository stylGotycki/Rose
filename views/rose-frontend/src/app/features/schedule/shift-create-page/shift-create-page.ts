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
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { EmployeeReadDto } from '../../../shared/models/employee.model';
import { ShiftPosition } from '../../../shared/models/schedule.model';
import { Employee } from '../../employees/employee';
import { Schedule } from '../schedule';

interface PositionOption {
  value: ShiftPosition;
  label: string;
}

@Component({
  selector: 'app-shift-create-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './shift-create-page.html',
  styleUrl: './shift-create-page.css',
})
export class ShiftCreatePage implements OnInit {
  private readonly scheduleService = inject(Schedule);
  private readonly employeeService = inject(Employee);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly employees = signal<EmployeeReadDto[]>([]);
  readonly loadingEmployees = signal(true);
  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);

  employeeId = '';

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

  constructor() {
    const start = new Date();

    start.setSeconds(0, 0);
    start.setMinutes(
      Math.ceil(start.getMinutes() / 15) * 15,
    );

    if (start.getMinutes() === 60) {
      start.setMinutes(0);
      start.setHours(start.getHours() + 1);
    }

    const end = new Date(
      start.getTime() +
        8 * 60 * 60 * 1000,
    );

    this.setStartFields(start);
    this.setEndFields(end);

    this.returnWeek = this.toDateInput(
      this.getStartOfWeek(start),
    );
  }

  ngOnInit(): void {
    this.applyQueryParams();
    this.loadEmployees();
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

  employeesLink(): string[] {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return ['/app/restaurants'];
    }

    return [
      '/app/restaurants',
      restaurantId,
      'panel',
      'employees',
    ];
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

    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );
      return;
    }

    if (!this.employeeId) {
      this.errorMessage.set(
        'Wybierz pracownika.',
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
      .create(restaurantId, {
        employeeId: this.employeeId,
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
                  'Zmiana została dodana do grafiku.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Shift create error:',
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
              'Nie masz uprawnień do tworzenia grafiku.',
            );
            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego pracownika.',
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
            'Nie udało się utworzyć zmiany.',
          );
        },
      });
  }

  private applyQueryParams(): void {
    const date =
      this.route.snapshot.queryParamMap.get(
        'date',
      );

    const startTime =
      this.route.snapshot.queryParamMap.get(
        'startTime',
      );

    const week =
      this.route.snapshot.queryParamMap.get(
        'week',
      );

    if (
      week &&
      /^\d{4}-\d{2}-\d{2}$/.test(week)
    ) {
      this.returnWeek = week;
    }

    if (
      !date ||
      !startTime ||
      !/^\d{4}-\d{2}-\d{2}$/.test(date) ||
      !/^\d{2}:\d{2}$/.test(startTime)
    ) {
      return;
    }

    const start = this.buildLocalDate(
      date,
      startTime,
    );

    if (!start) {
      return;
    }

    const end = new Date(
      start.getTime() +
        8 * 60 * 60 * 1000,
    );

    this.setStartFields(start);
    this.setEndFields(end);
  }

  private loadEmployees(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.loadingEmployees.set(false);

      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );
      return;
    }

    this.loadingEmployees.set(true);

    this.employeeService
      .getAllByRestaurant(restaurantId)
      .pipe(
        finalize(() =>
          this.loadingEmployees.set(false),
        ),
      )
      .subscribe({
        next: (response) => {
          const activeEmployees =
            (response.items ?? []).filter(
              (employee) =>
                employee.status === 'ACTIVE',
            );

          this.employees.set(
            activeEmployees,
          );

          if (
            activeEmployees.length > 0
          ) {
            this.employeeId =
              activeEmployees[0].id;
          }
        },
        error: (error) => {
          console.error(
            'Employee loading error:',
            error,
          );

          this.errorMessage.set(
            'Nie udało się pobrać pracowników.',
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