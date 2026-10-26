import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { CollectionRestDto } from '../../shared/models/collection-rest.model';
import {
  LocationCreateDto,
  LocationReadDto,
  LocationUpdateDto,
} from '../../shared/models/location.model';

@Injectable({
  providedIn: 'root',
})
export class Location {
  private readonly http = inject(HttpClient);

  getAllByRestaurant(
    restaurantId: string,
  ): Observable<CollectionRestDto<LocationReadDto>> {
    return this.http.get<CollectionRestDto<LocationReadDto>>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/locations`,
    );
  }

  getById(locationId: string): Observable<LocationReadDto> {
    return this.http.get<LocationReadDto>(
      `${API_BASE_URL}/api/locations/${locationId}`,
    );
  }

  create(
    restaurantId: string,
    request: LocationCreateDto,
  ): Observable<void> {
    return this.http.post<void>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/locations`,
      request,
    );
  }

  update(
    locationId: string,
    request: LocationUpdateDto,
  ): Observable<void> {
    return this.http.put<void>(
      `${API_BASE_URL}/api/locations/${locationId}`,
      request,
    );
  }

  archive(locationId: string): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/locations/${locationId}`,
    );
  }
}