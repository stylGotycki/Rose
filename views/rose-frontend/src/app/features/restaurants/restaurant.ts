import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import {
  Observable,
  filter,
  map,
  switchMap,
  take,
  timer,
} from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { CollectionRestDto } from '../../shared/models/collection-rest.model';
import {
  RestaurantCreateDto,
  RestaurantReadDto,
  RestaurantUpdateDto,
} from '../../shared/models/restaurant.model';

@Injectable({
  providedIn: 'root',
})
export class Restaurant {
  private readonly http = inject(HttpClient);

  getAll(): Observable<CollectionRestDto<RestaurantReadDto>> {
    return this.http.get<CollectionRestDto<RestaurantReadDto>>(
      `${API_BASE_URL}/api/restaurants`,
    );
  }

  getById(restaurantId: string): Observable<RestaurantReadDto> {
    return this.http.get<RestaurantReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}`,
    );
  }

  create(request: RestaurantCreateDto): Observable<void> {
    return this.http.post<void>(
      `${API_BASE_URL}/api/restaurants`,
      request,
    );
  }

  update(
    restaurantId: string,
    request: RestaurantUpdateDto,
  ): Observable<void> {
    return this.http.put<void>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}`,
      request,
    );
  }

  archive(restaurantId: string): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}`,
    );
  }

  waitUntilVisible(
    name: string,
    taxId: string | null,
  ): Observable<RestaurantReadDto> {
    const normalizedName = name.trim().toLowerCase();
    const normalizedTaxId = taxId?.trim() || null;

    return timer(0, 500).pipe(
      take(12),
      switchMap(() => this.getAll()),
      map((response) =>
        (response.items ?? []).find((item) => {
          const sameName =
            item.name.trim().toLowerCase() === normalizedName;

          const sameTaxId =
            normalizedTaxId === null ||
            (item.taxId?.trim() || null) === normalizedTaxId;

          return sameName && sameTaxId;
        }),
      ),
      filter(
        (
          restaurant,
        ): restaurant is RestaurantReadDto =>
          restaurant !== undefined,
      ),
      take(1),
    );
  }
}