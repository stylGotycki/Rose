import {
  Component,
  DestroyRef,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Router,
  RouterLink,
} from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  Subject,
  catchError,
  debounceTime,
  distinctUntilChanged,
  finalize,
  map,
  of,
  switchMap,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import {
  RestaurantMemberRole,
} from '../../../shared/models/member.model';
import { UserReadDto } from '../../../shared/models/user.model';
import { Member } from '../member';
import { UserDirectory } from '../user-directory';

interface MemberRoleOption {
  value: RestaurantMemberRole;
  label: string;
  description: string;
}

@Component({
  selector: 'app-member-create-page',
  imports: [
    FormsModule,
    RouterLink,
  ],
  templateUrl: './member-create-page.html',
  styleUrl: './member-create-page.css',
})
export class MemberCreatePage {
  private readonly memberService =
    inject(Member);

  private readonly userDirectory =
    inject(UserDirectory);

  private readonly router =
    inject(Router);

  private readonly destroyRef =
    inject(DestroyRef);

  readonly restaurantContext =
    inject(RestaurantContext);

  readonly searchResults =
    signal<UserReadDto[]>([]);

  readonly selectedUser =
    signal<UserReadDto | null>(null);

  readonly searching = signal(false);
  readonly saving = signal(false);

  readonly searchErrorMessage =
    signal<string | null>(null);

  readonly errorMessage =
    signal<string | null>(null);

  readonly roleOptions:
    readonly MemberRoleOption[] = [
      {
        value: 'MANAGER',
        label: 'Manager',
        description:
          'Zarządzanie restauracją, personelem i konfiguracją.',
      },
      {
        value: 'CHEF',
        label: 'Kucharz',
        description:
          'Dostęp do zamówień i widoku kuchni.',
      },
      {
        value: 'WAITER',
        label: 'Kelner',
        description:
          'Obsługa zamówień i pracy z klientami.',
      },
      {
        value: 'STAFF',
        label: 'Pracownik',
        description:
          'Podstawowy dostęp pracowniczy.',
      },
    ];

  emailQuery = '';
  role: RestaurantMemberRole = 'STAFF';

  private readonly searchTerms =
    new Subject<string>();

  constructor() {
    this.searchTerms
      .pipe(
        map((value) =>
          value.trim(),
        ),

        debounceTime(350),

        distinctUntilChanged(),

        switchMap((email) => {
          this.searchErrorMessage.set(null);

          if (!email) {
            this.searching.set(false);

            return of<UserReadDto[]>([]);
          }

          this.searching.set(true);

          return this.userDirectory
            .searchByEmail(email)
            .pipe(
              map(
                (response) =>
                  response.items ?? [],
              ),

              catchError((error) => {
                console.error(
                  'User search error:',
                  error,
                );

                if (error.status === 403) {
                  this.searchErrorMessage.set(
                    'Nie masz uprawnień do wyszukiwania użytkowników.',
                  );
                } else {
                  this.searchErrorMessage.set(
                    'Nie udało się wyszukać użytkowników.',
                  );
                }

                return of<UserReadDto[]>([]);
              }),

              finalize(() =>
                this.searching.set(false),
              ),
            );
        }),

        takeUntilDestroyed(
          this.destroyRef,
        ),
      )
      .subscribe((users) => {
        this.searchResults.set(users);
      });
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
      'members',
    ];
  }

  onSearchChange(
    value: string,
  ): void {
    this.emailQuery = value;

    const selected =
      this.selectedUser();

    if (
      selected &&
      selected.email !== value
    ) {
      this.selectedUser.set(null);
    }

    this.searchTerms.next(value);
  }

  selectUser(
    user: UserReadDto,
  ): void {
    this.selectedUser.set(user);
    this.emailQuery = user.email;
    this.searchResults.set([]);
    this.searchErrorMessage.set(null);
    this.errorMessage.set(null);
  }

  clearSelectedUser(): void {
    this.selectedUser.set(null);
    this.emailQuery = '';
    this.searchResults.set([]);
    this.searchErrorMessage.set(null);
  }

  fullName(
    user: UserReadDto,
  ): string {
    return [
      user.firstName,
      user.lastName,
    ]
      .filter(Boolean)
      .join(' ');
  }

  selectedRoleDescription(): string {
    return (
      this.roleOptions.find(
        (option) =>
          option.value === this.role,
      )?.description ?? ''
    );
  }

  submit(): void {
    this.errorMessage.set(null);

    const restaurantId =
      this.restaurantContext.restaurantId();

    const selectedUser =
      this.selectedUser();

    if (!restaurantId) {
      this.errorMessage.set(
        'Nie udało się ustalić wybranej restauracji.',
      );

      return;
    }

    if (!selectedUser) {
      this.errorMessage.set(
        'Wyszukaj i wybierz użytkownika z listy.',
      );

      return;
    }

    if (
      selectedUser.status !== 'ACTIVE'
    ) {
      this.errorMessage.set(
        'Dostęp można nadać wyłącznie aktywnemu użytkownikowi.',
      );

      return;
    }

    this.saving.set(true);

    this.memberService
      .create(
        restaurantId,
        {
          userId: selectedUser.id,
          role: this.role,
        },
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
                  `Nadano dostęp użytkownikowi ${selectedUser.email}. Powiadomienie e-mail zostanie wysłane przez system.`,
              },
            },
          );
        },
        error: (error) => {
          console.error(
            'Member creation error:',
            error,
          );

          if (error.status === 400) {
            this.errorMessage.set(
              'Nie można nadać użytkownikowi wybranej roli.',
            );

            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do nadawania dostępu.',
            );

            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Wybrany użytkownik nie istnieje lub został wyłączony.',
            );

            return;
          }

          if (error.status === 409) {
            this.errorMessage.set(
              'Wybrany użytkownik posiada już aktywny dostęp do tej restauracji.',
            );

            return;
          }

          this.errorMessage.set(
            'Nie udało się nadać dostępu użytkownikowi.',
          );
        },
      });
  }
}