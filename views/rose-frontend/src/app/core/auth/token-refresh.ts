import {
  HttpBackend,
  HttpClient,
} from '@angular/common/http';
import {
  Injectable,
  inject,
} from '@angular/core';
import {
  Observable,
  finalize,
  shareReplay,
  tap,
  throwError,
} from 'rxjs';

import { API_BASE_URL } from '../api/api.config';
import {
  RefreshRequestDto,
  TokenPairDto,
} from './auth.models';
import { TokenStorage } from './token-storage';

@Injectable({
  providedIn: 'root',
})
export class TokenRefresh {
  private readonly tokenStorage =
    inject(TokenStorage);

  private readonly http = new HttpClient(
    inject(HttpBackend),
  );

  private refreshRequest$:
    Observable<TokenPairDto> | null = null;

  refresh(): Observable<TokenPairDto> {
    if (this.refreshRequest$) {
      return this.refreshRequest$;
    }

    const refreshToken =
      this.tokenStorage.getRefreshToken();

    if (!refreshToken) {
      return throwError(
        () =>
          new Error(
            'Brak refresh tokenu.',
          ),
      );
    }

    const request: RefreshRequestDto = {
      refreshToken,
    };

    this.refreshRequest$ = this.http
      .post<TokenPairDto>(
        `${API_BASE_URL}/api/auth/refresh`,
        request,
      )
      .pipe(
        tap((tokens) => {
          if (
            !tokens.accessToken ||
            !tokens.refreshToken
          ) {
            throw new Error(
              'Backend nie zwrócił kompletnej pary tokenów.',
            );
          }

          this.tokenStorage.saveTokens(
            tokens.accessToken,
            tokens.refreshToken,
          );
        }),

        finalize(() => {
          this.refreshRequest$ = null;
        }),

        shareReplay({
          bufferSize: 1,
          refCount: false,
        }),
      );

    return this.refreshRequest$;
  }
}