import {
  Component,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Router,
  RouterLink,
} from '@angular/router';
import { finalize } from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import {
  EmployeeCreateDto,
  EmployeeRole,
} from '../../../shared/models/employee.model';
import { Employee } from '../employee';

interface EmployeeRoleOption {
  value: EmployeeRole;
  label: string;
}

@Component({
  selector: 'app-employee-create-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './employee-create-page.html',
  styleUrl: './employee-create-page.css',
})
export class EmployeeCreatePage {
  private readonly employeeService =
    inject(Employee);

  private readonly router =
    inject(Router);

  readonly restaurantContext =
    inject(RestaurantContext);

  readonly saving = signal(false);

  readonly errorMessage =
    signal<string | null>(null);

  readonly roleOptions:
    readonly EmployeeRoleOption[] = [
      {
        value: 'OWNER',
        label: 'Właściciel',
      },
      {
        value: 'MANAGER',
        label: 'Manager',
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
        value: 'STAFF',
        label: 'Pracownik',
      },
    ];

  firstName = '';
  lastName = '';
  email = '';
  phone = '';
  role: EmployeeRole = 'STAFF';

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

    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );

      return;
    }

    const firstName =
      this.firstName.trim();

    const lastName =
      this.lastName.trim();

    const email =
      this.email.trim().toLowerCase();

    const phone =
      this.phone.trim();

    if (!firstName) {
      this.errorMessage.set(
        'Imię pracownika jest wymagane.',
      );

      return;
    }

    if (!lastName) {
      this.errorMessage.set(
        'Nazwisko pracownika jest wymagane.',
      );

      return;
    }

    if (!this.isValidEmail(email)) {
      this.errorMessage.set(
        'Podaj poprawny adres e-mail.',
      );

      return;
    }

    if (
      firstName.length > 64 ||
      lastName.length > 64
    ) {
      this.errorMessage.set(
        'Imię i nazwisko mogą mieć maksymalnie 64 znaki.',
      );

      return;
    }

    if (email.length > 128) {
      this.errorMessage.set(
        'Adres e-mail może mieć maksymalnie 128 znaków.',
      );

      return;
    }

    if (phone.length > 32) {
      this.errorMessage.set(
        'Numer telefonu może mieć maksymalnie 32 znaki.',
      );

      return;
    }

    /*
     * Celowo nie przekazujemy userId.
     *
     * Utworzony pracownik będzie istniał
     * wyłącznie w Workforce Service.
     */
    const request: EmployeeCreateDto = {
      firstName,
      lastName,
      email,
      phone: phone || null,
      role: this.role,
    };

    this.saving.set(true);

    this.employeeService
      .create(
        restaurantId,
        request,
      )
      .pipe(
        finalize(() =>
          this.saving.set(false),
        ),
      )
      .subscribe({
        next: () => {
          this.router.navigate(
            this.listLink(),
            {
              state: {
                successMessage:
                  'Pracownik został dodany. Nie utworzono dla niego konta ani dostępu do panelu.',
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Employee creation error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Sprawdź poprawność danych pracownika.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do dodawania pracowników.',
            );

            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Pracownik o podanym adresie e-mail już istnieje.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się dodać pracownika.',
          );
        },
      });
  }

  private isValidEmail(
    email: string,
  ): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
      email,
    );
  }
}