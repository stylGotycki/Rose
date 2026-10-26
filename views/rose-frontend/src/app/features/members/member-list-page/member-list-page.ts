import { DatePipe } from '@angular/common';
import {
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  catchError,
  finalize,
  forkJoin,
  of,
} from 'rxjs';

import { RestaurantContext } from '../../../core/state/restaurant-context';
import {
  RestaurantMemberReadDto,
  RestaurantMemberRole,
} from '../../../shared/models/member.model';
import { Member } from '../member';

@Component({
  selector: 'app-member-list-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './member-list-page.html',
  styleUrl: './member-list-page.css',
})
export class MemberListPage implements OnInit {
  private readonly memberService = inject(Member);

  readonly restaurantContext = inject(RestaurantContext);

  readonly members = signal<RestaurantMemberReadDto[]>([]);
  readonly currentMember =
    signal<RestaurantMemberReadDto | null>(null);

  readonly loading = signal(true);
  readonly archivingId = signal<string | null>(null);

  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly memberToArchive =
    signal<RestaurantMemberReadDto | null>(null);

  readonly activeMembers = computed(() =>
    this.members().filter(
      (member) => member.status === 'ACTIVE',
    ),
  );

  readonly archivedMembers = computed(() =>
    this.members().filter(
      (member) => member.status === 'ARCHIVED',
    ),
  );

  ngOnInit(): void {
    const message = history.state?.successMessage;

    if (typeof message === 'string') {
      this.showSuccess(message);
    }

    this.loadMembers();
  }

  loadMembers(): void {
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
      members:
        this.memberService.getAllByRestaurant(restaurantId),
      currentMember:
        this.memberService
          .getCurrentMember(restaurantId)
          .pipe(
            catchError(() =>
              of<RestaurantMemberReadDto | null>(null),
            ),
          ),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ members, currentMember }) => {
          this.members.set(members.items ?? []);
          this.currentMember.set(currentMember);
        },
        error: (error) => {
          console.error('Member list error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do przeglądania członków restauracji.',
            );
            return;
          }

          this.errorMessage.set(
            'Nie udało się pobrać listy dostępów.',
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
      'members',
      'create',
    ];
  }

  editLink(memberId: string): string[] {
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
      memberId,
      'edit',
    ];
  }

  roleLabel(role: RestaurantMemberRole): string {
    const labels: Record<RestaurantMemberRole, string> = {
      OWNER: 'Właściciel',
      MANAGER: 'Menedżer',
      CHEF: 'Kucharz',
      WAITER: 'Kelner',
      STAFF: 'Pracownik',
    };

    return labels[role];
  }

  isCurrentMember(memberId: string): boolean {
    return this.currentMember()?.id === memberId;
  }

  showArchiveConfirmation(
    member: RestaurantMemberReadDto,
  ): void {
    this.memberToArchive.set(member);
  }

  hideArchiveConfirmation(): void {
    if (this.archivingId()) {
      return;
    }

    this.memberToArchive.set(null);
  }

  archiveMember(): void {
    const member = this.memberToArchive();

    if (!member) {
      return;
    }

    this.archivingId.set(member.id);
    this.errorMessage.set(null);

    this.memberService
      .archive(member.id)
      .pipe(finalize(() => this.archivingId.set(null)))
      .subscribe({
        next: () => {
          this.members.update((members) =>
            members.map((item) =>
              item.id === member.id
                ? {
                    ...item,
                    status: 'ARCHIVED',
                    updatedAt: new Date().toISOString(),
                  }
                : item,
            ),
          );

          this.memberToArchive.set(null);

          this.showSuccess(
            'Dostęp użytkownika został zarchiwizowany.',
          );
        },
        error: (error) => {
          console.error('Member archive error:', error);

          if (error.status === 403) {
            this.errorMessage.set(
              'Nie masz uprawnień do archiwizacji tego dostępu.',
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
            'Nie udało się zarchiwizować dostępu.',
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