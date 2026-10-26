import { DatePipe } from '@angular/common';
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
  catchError,
  finalize,
  forkJoin,
  of,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import { EmployeeReadDto } from '../../../shared/models/employee.model';
import {
  ScheduleShiftReadDto,
  ScheduleShiftStatus,
  ShiftPosition,
} from '../../../shared/models/schedule.model';
import { Employee } from '../../employees/employee';
import { Schedule } from '../schedule';

type ShiftAction = 'cancel' | 'complete';
type ScheduleViewMode = 'calendar' | 'list';
type PositionFilter = ShiftPosition | 'ALL';

interface PendingShiftAction {
  type: ShiftAction;
  shift: ScheduleShiftReadDto;
}

interface CalendarDay {
  date: Date;
  dateKey: string;
  shortName: string;
  isToday: boolean;
}

interface RawCalendarShiftSegment {
  shift: ScheduleShiftReadDto;
  dayKey: string;
  renderKey: string;
  top: number;
  height: number;
}

interface CalendarShiftSegment
  extends RawCalendarShiftSegment {
  laneIndex: number;
  laneCount: number;
  leftPercent: number;
  widthPercent: number;
}

interface PositionOption {
  value: ShiftPosition;
  label: string;
}

@Component({
  selector: 'app-schedule-page',
  imports: [
    RouterLink,
    DatePipe,
    FormsModule,
  ],
  templateUrl: './schedule-page.html',
  styleUrl: './schedule-page.css',
})
export class SchedulePage implements OnInit {
  private readonly scheduleService = inject(Schedule);
  private readonly employeeService = inject(Employee);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly startHour = 0;
  readonly endHour = 24;
  readonly hourHeight = 64;
  readonly slotMinutes = 30;
  readonly slotHeight = 32;

  readonly hours = Array.from(
    {
      length: this.endHour - this.startHour,
    },
    (_, index) => this.startHour + index,
  );

  readonly timeSlots = Array.from(
    {
      length:
        ((this.endHour - this.startHour) * 60) /
        this.slotMinutes,
    },
    (_, index) => index,
  );

  readonly calendarHeight =
    (this.endHour - this.startHour) *
    this.hourHeight;

  readonly positionOptions: PositionOption[] = [
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
      label: 'Sprzątanie',
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

  readonly weekStart = signal<Date>(
    this.getStartOfWeek(new Date()),
  );

  readonly shifts =
    signal<ScheduleShiftReadDto[]>([]);

  readonly employees =
    signal<EmployeeReadDto[]>([]);

  readonly loading = signal(true);

  readonly actionShiftId =
    signal<string | null>(null);

  readonly errorMessage =
    signal<string | null>(null);

  readonly successMessage =
    signal<string | null>(null);

  readonly selectedShift =
    signal<ScheduleShiftReadDto | null>(null);

  readonly pendingAction =
    signal<PendingShiftAction | null>(null);

  readonly viewMode =
    signal<ScheduleViewMode>('calendar');

  readonly showPlanned = signal(true);
  readonly showCompleted = signal(false);
  readonly showCancelled = signal(false);

  readonly selectedEmployeeId =
    signal<string>('ALL');

  readonly selectedPosition =
    signal<PositionFilter>('ALL');

  readonly sortedEmployees = computed(() =>
    [...this.employees()].sort(
      (first, second) => {
        const firstName =
          `${first.firstName} ${first.lastName}`;

        const secondName =
          `${second.firstName} ${second.lastName}`;

        return firstName.localeCompare(
          secondName,
          'pl',
        );
      },
    ),
  );

  readonly weekDays = computed<CalendarDay[]>(() => {
    const start = this.weekStart();

    const names = [
      'Pon',
      'Wt',
      'Śr',
      'Czw',
      'Pt',
      'Sob',
      'Niedz',
    ];

    return Array.from(
      { length: 7 },
      (_, index) => {
        const date = new Date(start);

        date.setDate(
          start.getDate() + index,
        );

        return {
          date,
          dateKey: this.toDateInput(date),
          shortName: names[index],
          isToday: this.isSameDay(
            date,
            new Date(),
          ),
        };
      },
    );
  });

  readonly weekLabel = computed(() => {
    const days = this.weekDays();

    const firstDay = days[0]?.date;
    const lastDay = days[6]?.date;

    if (!firstDay || !lastDay) {
      return '';
    }

    const formatter =
      new Intl.DateTimeFormat('pl-PL', {
        day: '2-digit',
        month: 'short',
      });

    if (
      firstDay.getFullYear() ===
      lastDay.getFullYear()
    ) {
      return (
        `${formatter.format(firstDay)} – ` +
        `${formatter.format(lastDay)} ` +
        `${lastDay.getFullYear()}`
      );
    }

    return (
      `${formatter.format(firstDay)} ` +
      `${firstDay.getFullYear()} – ` +
      `${formatter.format(lastDay)} ` +
      `${lastDay.getFullYear()}`
    );
  });

  readonly scopeFilteredShifts = computed(() =>
    this.shifts().filter((shift) => {
      const employeeMatches =
        this.selectedEmployeeId() === 'ALL' ||
        shift.employeeId ===
          this.selectedEmployeeId();

      const positionMatches =
        this.selectedPosition() === 'ALL' ||
        shift.position ===
          this.selectedPosition();

      return (
        employeeMatches &&
        positionMatches
      );
    }),
  );

  readonly filteredShifts = computed(() =>
    this.scopeFilteredShifts().filter(
      (shift) => {
        if (shift.status === 'PLANNED') {
          return this.showPlanned();
        }

        if (shift.status === 'COMPLETED') {
          return this.showCompleted();
        }

        if (shift.status === 'CANCELLED') {
          return this.showCancelled();
        }

        return false;
      },
    ),
  );

  readonly orderedFilteredShifts = computed(() =>
    [...this.filteredShifts()].sort(
      (first, second) =>
        new Date(first.startsAt).getTime() -
        new Date(second.startsAt).getTime(),
    ),
  );

  readonly plannedCount = computed(() =>
    this.scopeFilteredShifts().filter(
      (shift) =>
        shift.status === 'PLANNED',
    ).length,
  );

  readonly completedCount = computed(() =>
    this.scopeFilteredShifts().filter(
      (shift) =>
        shift.status === 'COMPLETED',
    ).length,
  );

  readonly cancelledCount = computed(() =>
    this.scopeFilteredShifts().filter(
      (shift) =>
        shift.status === 'CANCELLED',
    ).length,
  );

  readonly allStatusesVisible = computed(
    () =>
      this.showPlanned() &&
      this.showCompleted() &&
      this.showCancelled(),
  );

  readonly hasScopeFilters = computed(
    () =>
      this.selectedEmployeeId() !== 'ALL' ||
      this.selectedPosition() !== 'ALL',
  );

  readonly selectedEmployeeName = computed(() => {
    const employeeId =
      this.selectedEmployeeId();

    if (employeeId === 'ALL') {
      return 'Wszyscy pracownicy';
    }

    return this.employeeName(employeeId);
  });

  readonly selectedPositionName = computed(() => {
    const position =
      this.selectedPosition();

    if (position === 'ALL') {
      return 'Wszystkie stanowiska';
    }

    return this.positionLabel(position);
  });

  readonly calendarSegments = computed<
    CalendarShiftSegment[]
  >(() => {
    const result: CalendarShiftSegment[] = [];

    for (const day of this.weekDays()) {
      const dayStart = new Date(day.date);

      dayStart.setHours(0, 0, 0, 0);

      const dayEnd = new Date(dayStart);

      dayEnd.setDate(
        dayEnd.getDate() + 1,
      );

      const rawSegments:
        RawCalendarShiftSegment[] = [];

      for (const shift of this.filteredShifts()) {
        const shiftStart = new Date(
          shift.startsAt,
        );

        const shiftEnd = new Date(
          shift.endsAt,
        );

        if (
          Number.isNaN(
            shiftStart.getTime(),
          ) ||
          Number.isNaN(
            shiftEnd.getTime(),
          )
        ) {
          continue;
        }

        const segmentStartTimestamp =
          Math.max(
            shiftStart.getTime(),
            dayStart.getTime(),
          );

        const segmentEndTimestamp =
          Math.min(
            shiftEnd.getTime(),
            dayEnd.getTime(),
          );

        if (
          segmentEndTimestamp <=
          segmentStartTimestamp
        ) {
          continue;
        }

        const minutesFromDayStart =
          (
            segmentStartTimestamp -
            dayStart.getTime()
          ) /
          60_000;

        const durationMinutes =
          (
            segmentEndTimestamp -
            segmentStartTimestamp
          ) /
          60_000;

        rawSegments.push({
          shift,
          dayKey: day.dateKey,
          renderKey:
            `${shift.id}-${day.dateKey}`,
          top:
            (
              minutesFromDayStart /
              60
            ) *
            this.hourHeight,
          height: Math.max(
            28,
            (
              durationMinutes /
              60
            ) *
              this.hourHeight,
          ),
        });
      }

      result.push(
        ...this.layoutOverlappingSegments(
          rawSegments,
        ),
      );
    }

    return result;
  });

  ngOnInit(): void {
    this.applyFiltersFromQueryParams();

    const message =
      history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

    this.loadCalendar();
  }

  loadCalendar(): void {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.loading.set(false);

      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );

      return;
    }

    const from = new Date(
      this.weekStart(),
    );

    from.setHours(0, 0, 0, 0);

    const to = new Date(from);

    to.setDate(
      to.getDate() + 7,
    );

    this.loading.set(true);
    this.errorMessage.set(null);
    this.selectedShift.set(null);

    forkJoin({
      periodShifts:
        this.scheduleService
          .getByRestaurantAndPeriod(
            restaurantId,
            from.toISOString(),
            to.toISOString(),
          )
          .pipe(
            catchError((error) => {
              console.warn(
                'Nie udało się pobrać zmian z okresu:',
                error,
              );

              return of({
                items:
                  [] as ScheduleShiftReadDto[],
              });
            }),
          ),

      plannedShifts:
        this.scheduleService
          .getPlannedByRestaurant(
            restaurantId,
          )
          .pipe(
            catchError((error) => {
              console.warn(
                'Nie udało się pobrać zaplanowanych zmian:',
                error,
              );

              return of({
                items:
                  [] as ScheduleShiftReadDto[],
              });
            }),
          ),

      employees:
        this.employeeService
          .getAllByRestaurant(
            restaurantId,
          ),
    })
      .pipe(
        finalize(() =>
          this.loading.set(false),
        ),
      )
      .subscribe({
        next: ({
          periodShifts,
          plannedShifts,
          employees,
        }) => {
          const fromTimestamp =
            from.getTime();

          const toTimestamp =
            to.getTime();

          const combined = [
            ...(periodShifts.items ?? []),
            ...(plannedShifts.items ?? []),
          ];

          const uniqueShifts =
            new Map<
              string,
              ScheduleShiftReadDto
            >();

          for (const shift of combined) {
            const startsAt = new Date(
              shift.startsAt,
            ).getTime();

            const endsAt = new Date(
              shift.endsAt,
            ).getTime();

            if (
              Number.isNaN(startsAt) ||
              Number.isNaN(endsAt)
            ) {
              continue;
            }

            const overlapsWeek =
              endsAt > fromTimestamp &&
              startsAt < toTimestamp;

            if (overlapsWeek) {
              uniqueShifts.set(
                shift.id,
                shift,
              );
            }
          }

          this.shifts.set(
            Array.from(
              uniqueShifts.values(),
            ),
          );

          this.employees.set(
            employees.items ?? [],
          );

          this.validateEmployeeFilter();
        },
        error: (error) => {
          console.error(
            'Schedule calendar error:',
            error,
          );

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania grafiku.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać grafiku.',
          );
        },
      });
  }

  previousWeek(): void {
    const date = new Date(
      this.weekStart(),
    );

    date.setDate(
      date.getDate() - 7,
    );

    this.weekStart.set(date);
    this.loadCalendar();
  }

  nextWeek(): void {
    const date = new Date(
      this.weekStart(),
    );

    date.setDate(
      date.getDate() + 7,
    );

    this.weekStart.set(date);
    this.loadCalendar();
  }

  goToCurrentWeek(): void {
    this.weekStart.set(
      this.getStartOfWeek(new Date()),
    );

    this.loadCalendar();
  }

  setViewMode(
    mode: ScheduleViewMode,
  ): void {
    this.viewMode.set(mode);
    this.selectedShift.set(null);
  }

  changeEmployeeFilter(
    employeeId: string,
  ): void {
    this.selectedEmployeeId.set(
      employeeId,
    );

    this.selectedShift.set(null);
    this.updateQueryParams();
  }

  changePositionFilter(
    position: PositionFilter,
  ): void {
    this.selectedPosition.set(
      position,
    );

    this.selectedShift.set(null);
    this.updateQueryParams();
  }

  clearScopeFilters(): void {
    this.selectedEmployeeId.set('ALL');
    this.selectedPosition.set('ALL');
    this.selectedShift.set(null);

    this.updateQueryParams();
  }

  toggleStatus(
    status: ScheduleShiftStatus,
  ): void {
    if (status === 'PLANNED') {
      this.showPlanned.update(
        (value) => !value,
      );
    }

    if (status === 'COMPLETED') {
      this.showCompleted.update(
        (value) => !value,
      );
    }

    if (status === 'CANCELLED') {
      this.showCancelled.update(
        (value) => !value,
      );
    }

    this.selectedShift.set(null);
  }

  showAllStatuses(): void {
    this.showPlanned.set(true);
    this.showCompleted.set(true);
    this.showCancelled.set(true);

    this.selectedShift.set(null);
  }

  showOnlyPlanned(): void {
    this.showPlanned.set(true);
    this.showCompleted.set(false);
    this.showCancelled.set(false);

    this.selectedShift.set(null);
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
      'schedule',
      'create',
    ];
  }

  editLink(
    shiftId: string,
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
      'schedule',
      shiftId,
      'edit',
    ];
  }

  weekQueryParams(): Record<string, string> {
    return {
      week: this.toDateInput(
        this.weekStart(),
      ),
    };
  }

  createAt(
    dateKey: string,
    slotIndex: number,
  ): void {
    const totalMinutes =
      slotIndex * this.slotMinutes;

    const hours = Math.floor(
      totalMinutes / 60,
    );

    const minutes =
      totalMinutes % 60;

    const startTime =
      `${String(hours).padStart(2, '0')}:` +
      `${String(minutes).padStart(2, '0')}`;

    this.router.navigate(
      this.createLink(),
      {
        queryParams: {
          date: dateKey,
          startTime,
          week: this.toDateInput(
            this.weekStart(),
          ),
        },
      },
    );
  }

  segmentsForDay(
    dayKey: string,
  ): CalendarShiftSegment[] {
    return this.calendarSegments().filter(
      (segment) =>
        segment.dayKey === dayKey,
    );
  }

  openShiftDetails(
    shift: ScheduleShiftReadDto,
  ): void {
    this.selectedShift.set(shift);
  }

  closeShiftDetails(): void {
    this.selectedShift.set(null);
  }

  startAction(
    type: ShiftAction,
    shift: ScheduleShiftReadDto,
  ): void {
    this.selectedShift.set(null);

    this.pendingAction.set({
      type,
      shift,
    });
  }

  closeAction(): void {
    if (this.actionShiftId()) {
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

    this.actionShiftId.set(
      action.shift.id,
    );

    this.errorMessage.set(null);

    const request$ =
      action.type === 'cancel'
        ? this.scheduleService.cancel(
            action.shift.id,
          )
        : this.scheduleService.complete(
            action.shift.id,
          );

    request$
      .pipe(
        finalize(() =>
          this.actionShiftId.set(null),
        ),
      )
      .subscribe({
        next: () => {
          const newStatus:
            ScheduleShiftStatus =
              action.type === 'cancel'
                ? 'CANCELLED'
                : 'COMPLETED';

          this.shifts.update(
            (shifts) =>
              shifts.map((shift) =>
                shift.id ===
                action.shift.id
                  ? {
                      ...shift,
                      status: newStatus,
                      updatedAt:
                        new Date()
                          .toISOString(),
                    }
                  : shift,
              ),
          );

          this.pendingAction.set(null);

          this.showSuccess(
            action.type === 'cancel'
              ? 'Zmiana została anulowana.'
              : 'Zmiana została zakończona.',
          );
        },
        error: (error) => {
          console.error(
            'Schedule action error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Nie można wykonać tej operacji dla wybranej zmiany.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do zmiany statusu grafiku.',
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
            'Nie udało się zaktualizować zmiany.',
          );
        },
      });
  }

  employeeName(
    employeeId: string,
  ): string {
    const employee =
      this.employees().find(
        (item) =>
          item.id === employeeId,
      );

    if (!employee) {
      return (
        'Pracownik ' +
        employeeId.slice(0, 8)
      );
    }

    return (
      `${employee.firstName} ` +
      `${employee.lastName}`
    );
  }

  employeeInitials(
    employeeId: string,
  ): string {
    const employee =
      this.employees().find(
        (item) =>
          item.id === employeeId,
      );

    if (!employee) {
      return '?';
    }

    return (
      employee.firstName.charAt(0) +
      employee.lastName.charAt(0)
    ).toUpperCase();
  }

  positionLabel(
    position: ShiftPosition,
  ): string {
    const labels: Record<
      ShiftPosition,
      string
    > = {
      MANAGER: 'Menedżer',
      CHEF: 'Kucharz',
      WAITER: 'Kelner',
      BARTENDER: 'Barman',
      CASHIER: 'Kasjer',
      CLEANER: 'Sprzątanie',
      DELIVERY: 'Dostawca',
      STAFF: 'Pracownik',
    };

    return labels[position];
  }

  statusLabel(
    status: ScheduleShiftStatus,
  ): string {
    const labels: Record<
      ScheduleShiftStatus,
      string
    > = {
      PLANNED: 'Zaplanowana',
      CANCELLED: 'Anulowana',
      COMPLETED: 'Zakończona',
    };

    return labels[status];
  }

  hourLabel(
    hour: number,
  ): string {
    return (
      `${String(hour).padStart(2, '0')}:00`
    );
  }

  private validateEmployeeFilter(): void {
    const selectedId =
      this.selectedEmployeeId();

    if (selectedId === 'ALL') {
      return;
    }

    const employeeExists =
      this.employees().some(
        (employee) =>
          employee.id === selectedId,
      );

    if (!employeeExists) {
      this.selectedEmployeeId.set('ALL');
    }
  }

  private layoutOverlappingSegments(
    segments: RawCalendarShiftSegment[],
  ): CalendarShiftSegment[] {
    if (segments.length === 0) {
      return [];
    }

    const sorted = [...segments].sort(
      (first, second) =>
        first.top - second.top ||
        first.height - second.height,
    );

    const groups:
      RawCalendarShiftSegment[][] = [];

    let currentGroup:
      RawCalendarShiftSegment[] = [];

    let currentGroupEnd = -1;

    for (const segment of sorted) {
      const segmentEnd =
        segment.top + segment.height;

      if (
        currentGroup.length === 0 ||
        segment.top < currentGroupEnd
      ) {
        currentGroup.push(segment);

        currentGroupEnd = Math.max(
          currentGroupEnd,
          segmentEnd,
        );

        continue;
      }

      groups.push(currentGroup);

      currentGroup = [segment];
      currentGroupEnd = segmentEnd;
    }

    if (currentGroup.length > 0) {
      groups.push(currentGroup);
    }

    const result:
      CalendarShiftSegment[] = [];

    for (const group of groups) {
      const laneEnds: number[] = [];

      const assigned = group.map(
        (segment) => {
          let laneIndex =
            laneEnds.findIndex(
              (laneEnd) =>
                laneEnd <= segment.top,
            );

          if (laneIndex === -1) {
            laneIndex =
              laneEnds.length;

            laneEnds.push(
              segment.top +
                segment.height,
            );
          } else {
            laneEnds[laneIndex] =
              segment.top +
              segment.height;
          }

          return {
            segment,
            laneIndex,
          };
        },
      );

      const laneCount = Math.max(
        laneEnds.length,
        1,
      );

      const laneWidth =
        100 / laneCount;

      for (const item of assigned) {
        result.push({
          ...item.segment,
          laneIndex:
            item.laneIndex,
          laneCount,
          leftPercent:
            item.laneIndex *
              laneWidth +
            0.4,
          widthPercent: Math.max(
            2,
            laneWidth - 0.8,
          ),
        });
      }
    }

    return result;
  }

  private applyFiltersFromQueryParams(): void {
    const queryParams =
      this.route.snapshot.queryParamMap;

    const week =
      queryParams.get('week');

    const employeeId =
      queryParams.get('employeeId');

    const position =
      queryParams.get('position');

    if (
      week &&
      /^\d{4}-\d{2}-\d{2}$/.test(week)
    ) {
      const date = new Date(
        `${week}T00:00:00`,
      );

      if (
        !Number.isNaN(
          date.getTime(),
        )
      ) {
        this.weekStart.set(
          this.getStartOfWeek(date),
        );
      }
    }

    if (employeeId) {
      this.selectedEmployeeId.set(
        employeeId,
      );
    }

    if (
      position &&
      this.isShiftPosition(position)
    ) {
      this.selectedPosition.set(
        position,
      );
    }
  }

  private updateQueryParams(): void {
    const employeeId =
      this.selectedEmployeeId();

    const position =
      this.selectedPosition();

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        week: this.toDateInput(
          this.weekStart(),
        ),
        employeeId:
          employeeId === 'ALL'
            ? null
            : employeeId,
        position:
          position === 'ALL'
            ? null
            : position,
      },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }

  private isShiftPosition(
    value: string,
  ): value is ShiftPosition {
    return this.positionOptions.some(
      (option) =>
        option.value === value,
    );
  }

  private getStartOfWeek(
    date: Date,
  ): Date {
    const result = new Date(date);
    const day = result.getDay();

    const difference =
      day === 0
        ? -6
        : 1 - day;

    result.setDate(
      result.getDate() +
      difference,
    );

    result.setHours(
      0,
      0,
      0,
      0,
    );

    return result;
  }

  private isSameDay(
    first: Date,
    second: Date,
  ): boolean {
    return (
      first.getFullYear() ===
        second.getFullYear() &&
      first.getMonth() ===
        second.getMonth() &&
      first.getDate() ===
        second.getDate()
    );
  }

  private toDateInput(
    date: Date,
  ): string {
    const year =
      date.getFullYear();

    const month = String(
      date.getMonth() + 1,
    ).padStart(2, '0');

    const day = String(
      date.getDate(),
    ).padStart(2, '0');

    return `${year}-${month}-${day}`;
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