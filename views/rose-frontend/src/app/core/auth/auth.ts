import { HttpClient } from '@angular/common/http';
import {
  computed,
  Injectable,
  inject,
  signal,
} from '@angular/core';
import {
  Observable,
  map,
  of,
  switchMap,
  tap,
} from 'rxjs';

import { API_BASE_URL } from '../api/api.config';
import {
  ChangePasswordRequestDto,
  ForgotPasswordRequestDto,
  LoginRequestDto,
  RefreshRequestDto,
  RegisterRequestDto,
  ResetPasswordRequestDto,
  SessionDto,
  TokenPairDto,
  UserMeDto,
} from './auth.models';
import { TokenRefresh } from './token-refresh';
import { TokenStorage } from './token-storage';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http =
    inject(HttpClient);

  private readonly tokenStorage =
    inject(TokenStorage);

  private readonly tokenRefresh =
    inject(TokenRefresh);

  readonly currentUser =
    signal<UserMeDto | null>(null);

  readonly userLoading =
    signal(false);

  readonly authenticated = computed(
    () =>
      this.currentUser() !== null ||
      this.tokenStorage.isLoggedIn(),
  );

  readonly userFirstName = computed(
    () =>
      this.currentUser()?.firstName ??
      '',
  );

  readonly userInitial = computed(() => {
    const firstName =
      this.currentUser()?.firstName;

    return (
      firstName
        ?.charAt(0)
        .toUpperCase() ?? '?'
    );
  });

  constructor() {
    if (
      this.tokenStorage.isLoggedIn()
    ) {
      this.loadCurrentUser();
    }
  }

  login(
    request: LoginRequestDto,
  ): Observable<TokenPairDto> {
    return this.http
      .post<TokenPairDto>(
        `${API_BASE_URL}/api/auth/login`,
        request,
      )
      .pipe(
        tap((tokens) => {
          this.tokenStorage.saveTokens(
            tokens.accessToken,
            tokens.refreshToken,
          );
        }),

        switchMap((tokens) =>
          this.me().pipe(
            tap((user) =>
              this.currentUser.set(user),
            ),

            map(() => tokens),
          ),
        ),
      );
  }

  register(
    request: RegisterRequestDto,
  ): Observable<void> {
    return this.http.post<void>(
      `${API_BASE_URL}/api/auth/register`,
      request,
    );
  }

  refresh(): Observable<TokenPairDto> {
    return this.tokenRefresh.refresh();
  }

  logout(): Observable<void> {
    const refreshToken =
      this.tokenStorage.getRefreshToken();

    if (!refreshToken) {
      this.clearLocalSession();

      return of(undefined);
    }

    const request: RefreshRequestDto = {
      refreshToken,
    };

    return this.http
      .post<void>(
        `${API_BASE_URL}/api/auth/logout`,
        request,
      )
      .pipe(
        tap(() =>
          this.clearLocalSession(),
        ),
      );
  }

  me(): Observable<UserMeDto> {
    return this.http.get<UserMeDto>(
      `${API_BASE_URL}/api/auth/me`,
    );
  }

  loadCurrentUser(): void {
    if (
      !this.tokenStorage.isLoggedIn()
    ) {
      this.currentUser.set(null);

      return;
    }

    this.userLoading.set(true);

    this.me().subscribe({
      next: (user) => {
        this.currentUser.set(user);
        this.userLoading.set(false);
      },

      error: () => {
        /*
         * Interceptor najpierw spróbuje
         * odświeżyć access token.
         *
         * Ten fragment wykona się dopiero,
         * gdy odświeżenie również zawiedzie.
         */
        this.clearLocalSession();
        this.userLoading.set(false);
      },
    });
  }

  session(): Observable<SessionDto> {
    return this.http.get<SessionDto>(
      `${API_BASE_URL}/api/auth/session`,
    );
  }

  verify(
    token: string,
  ): Observable<void> {
    return this.http.get<void>(
      `${API_BASE_URL}/api/auth/verify`,
      {
        params: {
          token,
        },
      },
    );
  }

  forgotPassword(
    request:
      ForgotPasswordRequestDto,
  ): Observable<void> {
    return this.http.post<void>(
      `${API_BASE_URL}/api/auth/password/forgot`,
      request,
    );
  }

  resetPassword(
    request:
      ResetPasswordRequestDto,
  ): Observable<void> {
    return this.http.post<void>(
      `${API_BASE_URL}/api/auth/password/reset`,
      request,
    );
  }

  changePassword(
    request:
      ChangePasswordRequestDto,
  ): Observable<void> {
    return this.http.post<void>(
      `${API_BASE_URL}/api/auth/password/change`,
      request,
    );
  }

  clearLocalSession(): void {
    this.tokenStorage.clear();
    this.currentUser.set(null);
  }

  isLoggedIn(): boolean {
    return this.tokenStorage.isLoggedIn();
  }
}