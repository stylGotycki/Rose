import { DatePipe } from '@angular/common';
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
  EmployeeReadDto,
  EmployeeRole,
} from '../../../shared/models/employee.model';
import { Employee } from '../employee';

@Component({
  selector: 'app-employee-list-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './employee-list-page.html',
  styleUrl: './employee-list-page.css',
})
export class EmployeeListPage implements OnInit {
  private readonly employeeService = inject(Employee);

  readonly restaurantContext = inject(RestaurantContext);

  readonly employees = signal<EmployeeReadDto[]>([]);
  readonly loading = signal(true);
  readonly archivingId = signal<string | null>(null);

  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly employeeToArchive =
    signal<EmployeeReadDto | null>(null);

  readonly activeEmployees = computed(() =>
    this.employees().filter(
      (employee) => employee.status === 'ACTIVE',
    ),
  );

  readonly archivedEmployees = computed(() =>
    this.employees().filter(
      (employee) => employee.status === 'ARCHIVED',
    ),
  );

  ngOnInit(): void {
    const message = history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

    this.loadEmployees();
  }

  loadEmployees(): void {
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

    this.employeeService
      .getAllByRestaurant(restaurantId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.employees.set(response.items ?? []);
        },
        error: (error) => {
          console.error('Employee list error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania pracowników.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać listy pracowników.',
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
      'employees',
      'create',
    ];
  }

  editLink(employeeId: string): string[] {
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
      employeeId,
      'edit',
    ];
  }

  roleLabel(role: EmployeeRole): string {
    const labels: Record<EmployeeRole, string> = {
      OWNER: 'Właściciel',
      MANAGER: 'Menedżer',
      CHEF: 'Kucharz',
      WAITER: 'Kelner',
      STAFF: 'Pracownik',
    };

    return labels[role];
  }

  initials(employee: EmployeeReadDto): string {
    const firstInitial =
      employee.firstName.charAt(0).toUpperCase();

    const lastInitial =
      employee.lastName.charAt(0).toUpperCase();

    return `${firstInitial}${lastInitial}`;
  }

  showArchiveConfirmation(
    employee: EmployeeReadDto,
  ): void {
    this.employeeToArchive.set(employee);
  }

  hideArchiveConfirmation(): void {
    if (this.archivingId()) {
      return;
    }

    this.employeeToArchive.set(null);
  }

  archiveEmployee(): void {
    const employee = this.employeeToArchive();

    if (!employee) {
      return;
    }

    this.archivingId.set(employee.id);
    this.errorMessage.set(null);

    this.employeeService
      .archive(employee.id)
      .pipe(finalize(() => this.archivingId.set(null)))
      .subscribe({
        next: () => {
          this.employees.update((employees) =>
            employees.map((item) =>
              item.id === employee.id
                ? {
                    ...item,
                    status: 'ARCHIVED',
                    updatedAt: new Date().toISOString(),
                  }
                : item,
            ),
          );

          this.employeeToArchive.set(null);

          this.showSuccess(
            `Pracownik ${employee.firstName} ${employee.lastName} został przeniesiony do archiwum.`,
          );
        },
        error: (error) => {
          console.error('Employee archive error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do archiwizacji pracownika.',
            );
            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego pracownika.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się zarchiwizować pracownika.',
          );
        },
      });
  }

  private showSuccess(message: string): void {
    this.successMessage.set(message);

    window.setTimeout(() => {
      if (this.successMessage() === message) {
        this.successMessage.set(null);
      }
    }, 4000);
  }
}