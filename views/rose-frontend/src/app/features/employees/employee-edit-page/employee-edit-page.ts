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
import {
  EmployeeReadDto,
  EmployeeRole,
} from '../../../shared/models/employee.model';
import { Employee } from '../employee';

interface RoleOption {
  value: EmployeeRole;
  label: string;
}

@Component({
  selector: 'app-employee-edit-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './employee-edit-page.html',
  styleUrl: './employee-edit-page.css',
})
export class EmployeeEditPage implements OnInit {
  private readonly employeeService = inject(Employee);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly employee =
    signal<EmployeeReadDto | null>(null);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);

  firstName = '';
  lastName = '';
  email = '';
  phone = '';
  role: EmployeeRole = 'STAFF';

  readonly roles: RoleOption[] = [
    { value: 'OWNER', label: 'Właściciel' },
    { value: 'MANAGER', label: 'Menedżer' },
    { value: 'CHEF', label: 'Kucharz' },
    { value: 'WAITER', label: 'Kelner' },
    { value: 'STAFF', label: 'Pracownik' },
  ];

  ngOnInit(): void {
    const employeeId =
      this.route.snapshot.paramMap.get('employeeId');

    if (!employeeId) {
      this.loading.set(false);
      this.errorMessage.set(
        'Brak identyfikatora pracownika.',
      );
      return;
    }

    this.loadEmployee(employeeId);
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
      'employees',
    ];
  }

  submit(): void {
    this.errorMessage.set(null);

    const employee = this.employee();

    if (!employee) {
      this.errorMessage.set(
        'Nie udało się ustalić edytowanego pracownika.',
      );
      return;
    }

    const firstName = this.firstName.trim();
    const lastName = this.lastName.trim();
    const email = this.email.trim();
    const phone = this.phone.trim();

    if (!firstName || !lastName || !email) {
      this.errorMessage.set(
        'Imię, nazwisko i adres email są wymagane.',
      );
      return;
    }

    if (
      firstName.length > 64 ||
      lastName.length > 64 ||
      email.length > 128 ||
      phone.length > 32
    ) {
      this.errorMessage.set(
        'Jedno z pól przekracza dozwoloną długość.',
      );
      return;
    }

    if (!this.isValidEmail(email)) {
      this.errorMessage.set(
        'Podaj poprawny adres email.',
      );
      return;
    }

    this.saving.set(true);

    this.employeeService
      .update(employee.id, {
        firstName,
        lastName,
        email,
        phone: phone || null,
        role: this.role,
      })
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.router.navigate(this.listLink(), {
            state: {
              successMessage:
                `Dane pracownika ${firstName} ${lastName} zostały zapisane.`,
            },
          });
        },
        error: (error) => {
          console.error('Employee update error:', error);

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność wprowadzonych danych.',
            );
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do edycji pracownika.',
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
              'Inny pracownik korzysta już z podanego adresu email.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się zapisać zmian.',
          );
        },
      });
  }

  private loadEmployee(employeeId: string): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.employeeService
      .getById(employeeId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (employee) => {
          const restaurantId =
            this.restaurantContext.restaurantId();

          if (
            restaurantId &&
            employee.restaurantId !== restaurantId
          ) {
            this.errorMessage.set(
              'Wybrany pracownik nie należy do tej restauracji.',
            );
            return;
          }

          if (employee.status !== 'ACTIVE') {
            this.errorMessage.set(
              'Nie można edytować zarchiwizowanego pracownika.',
            );
            return;
          }

          this.employee.set(employee);

          this.firstName = employee.firstName;
          this.lastName = employee.lastName;
          this.email = employee.email;
          this.phone = employee.phone ?? '';
          this.role = employee.role;
        },
        error: (error) => {
          console.error('Employee details error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do wybranego pracownika.',
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
            'Nie udało się pobrać danych pracownika.',
          );
        },
      });
  }

  private isValidEmail(value: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
  }
}