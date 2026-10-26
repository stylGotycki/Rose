import {
  HttpErrorResponse,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import {
  catchError,
  switchMap,
  throwError,
} from 'rxjs';

import { TokenRefresh } from '../auth/token-refresh';
import { TokenStorage } from '../auth/token-storage';

const PUBLIC_AUTH_ENDPOINTS = [
  '/api/auth/login',
  '/api/auth/register',
  '/api/auth/refresh',
  '/api/auth/verify',
  '/api/auth/password/forgot',
  '/api/auth/password/reset',
];

const REFRESH_EXCLUDED_ENDPOINTS = [
  ...PUBLIC_AUTH_ENDPOINTS,
  '/api/auth/logout',
];

export const authInterceptor:
  HttpInterceptorFn = (
    request,
    next,
  ) => {
    const tokenStorage =
      inject(TokenStorage);

    const tokenRefresh =
      inject(TokenRefresh);

    if (!isApiRequest(request.url)) {
      return next(request);
    }

    const accessToken =
      tokenStorage.getAccessToken();

    const requestWithToken =
      accessToken &&
      !matchesAnyEndpoint(
        request.url,
        PUBLIC_AUTH_ENDPOINTS,
      )
        ? addAccessToken(
            request,
            accessToken,
          )
        : request;

    return next(requestWithToken).pipe(
      catchError((error: unknown) => {
        if (
          !shouldAttemptRefresh(
            error,
            request.url,
            tokenStorage,
          )
        ) {
          return throwError(
            () => error,
          );
        }

        const newestAccessToken =
          tokenStorage.getAccessToken();

        if (
          newestAccessToken &&
          newestAccessToken !== accessToken
        ) {
          return next(
            addAccessToken(
              request,
              newestAccessToken,
            ),
          );
        }

        return tokenRefresh
          .refresh()
          .pipe(
            switchMap((tokens) =>
              next(
                addAccessToken(
                  request,
                  tokens.accessToken,
                ),
              ),
            ),

            catchError(
              (refreshError: unknown) => {
                tokenStorage.clear();

                redirectToLogin();

                return throwError(
                  () => refreshError,
                );
              },
            ),
          );
      }),
    );
  };

function shouldAttemptRefresh(
  error: unknown,
  requestUrl: string,
  tokenStorage: TokenStorage,
): boolean {
  if (
    !(error instanceof HttpErrorResponse)
  ) {
    return false;
  }

  if (error.status !== 401) {
    return false;
  }

  if (
    matchesAnyEndpoint(
      requestUrl,
      REFRESH_EXCLUDED_ENDPOINTS,
    )
  ) {
    return false;
  }

  return (
    tokenStorage.getRefreshToken() !== null
  );
}

function addAccessToken<T>(
  request: HttpRequest<T>,
  accessToken: string,
): HttpRequest<T> {
  return request.clone({
    setHeaders: {
      Authorization:
        `Bearer ${accessToken}`,
    },
  });
}

function isApiRequest(
  url: string,
): boolean {
  return (
    url.startsWith('/api/') ||
    url.includes('/api/')
  );
}

function matchesAnyEndpoint(
  url: string,
  endpoints: string[],
): boolean {
  return endpoints.some(
    (endpoint) =>
      url.includes(endpoint),
  );
}

function redirectToLogin(): void {
  if (typeof window === 'undefined') {
    return;
  }

  if (
    window.location.pathname ===
    '/login'
  ) {
    return;
  }

  window.location.assign('/login');
}