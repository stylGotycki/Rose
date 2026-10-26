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
  RestaurantMemberReadDto,
  RestaurantMemberRole,
} from '../../../shared/models/member.model';
import { Member } from '../member';

interface RoleOption {
  value: RestaurantMemberRole;
  label: string;
}

@Component({
  selector: 'app-member-edit-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './member-edit-page.html',
  styleUrl: './member-edit-page.css',
})
export class MemberEditPage implements OnInit {
  private readonly memberService = inject(Member);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  readonly member =
    signal<RestaurantMemberReadDto | null>(null);

  readonly loading = signal(true);
  readonly saving = signal(false);

  readonly errorMessage = signal<string | null>(null);

  role: RestaurantMemberRole = 'STAFF';

  readonly roles: RoleOption[] = [
    { value: 'OWNER', label: 'Właściciel' },
    { value: 'MANAGER', label: 'Menedżer' },
    { value: 'CHEF', label: 'Kucharz' },
    { value: 'WAITER', label: 'Kelner' },
    { value: 'STAFF', label: 'Pracownik' },
  ];

  ngOnInit(): void {
    const memberId =
      this.route.snapshot.paramMap.get('memberId');

    if (!memberId) {
      this.loading.set(false);
      this.errorMessage.set(
        'Brak identyfikatora członka.',
      );
      return;
    }

    this.loadMember(memberId);
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

  submit(): void {
    this.errorMessage.set(null);

    const member = this.member();

    if (!member) {
      this.errorMessage.set(
        'Nie udało się ustalić edytowanego członka.',
      );
      return;
    }

    this.saving.set(true);

    this.memberService
      .updateRole(member.id, {
        role: this.role,
      })
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.router.navigate(this.listLink(), {
            state: {
              successMessage:
                'Rola użytkownika została zaktualizowana.',
            },
          });
        },
        error: (error) => {
          console.error('Member update error:', error);

          if (error.status === 400) {
            this.errorMessage.set(
              'Wybrana rola jest niepoprawna.',
            );
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do zmiany tej roli.',
            );
            return;
          }

          if (error.status === 404) {
            this.errorMessage.set(
              'Nie znaleziono wybranego członka.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się zaktualizować roli.',
          );
        },
      });
  }

  private loadMember(memberId: string): void {
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

    this.memberService
      .getAllByRestaurant(restaurantId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          const member = (response.items ?? []).find(
            (item) => item.id === memberId,
          );

          if (!member) {
            this.errorMessage.set(
              'Nie znaleziono wybranego członka.',
            );
            return;
          }

          if (member.status !== 'ACTIVE') {
            this.errorMessage.set(
              'Nie można edytować zarchiwizowanego dostępu.',
            );
            return;
          }

          this.member.set(member);
          this.role = member.role;
        },
        error: (error) => {
          console.error('Member details error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz dostępu do listy członków.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać danych członka.',
          );
        },
      });
  }
}