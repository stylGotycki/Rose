import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { CollectionRestDto } from '../../shared/models/collection-rest.model';
import {
  RestaurantMemberCreateDto,
  RestaurantMemberReadDto,
  RestaurantMemberUpdateDto,
} from '../../shared/models/member.model';

@Injectable({
  providedIn: 'root',
})
export class Member {
  private readonly http = inject(HttpClient);

  getAllByRestaurant(
    restaurantId: string,
  ): Observable<CollectionRestDto<RestaurantMemberReadDto>> {
    return this.http.get<CollectionRestDto<RestaurantMemberReadDto>>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/members`,
    );
  }

  getCurrentMember(
    restaurantId: string,
  ): Observable<RestaurantMemberReadDto> {
    return this.http.get<RestaurantMemberReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/members/me`,
    );
  }

  create(
    restaurantId: string,
    request: RestaurantMemberCreateDto,
  ): Observable<RestaurantMemberReadDto> {
    return this.http.post<RestaurantMemberReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/members`,
      request,
    );
  }

  updateRole(
    memberId: string,
    request: RestaurantMemberUpdateDto,
  ): Observable<RestaurantMemberReadDto> {
    return this.http.put<RestaurantMemberReadDto>(
      `${API_BASE_URL}/api/restaurant-members/${memberId}`,
      request,
    );
  }

  archive(memberId: string): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/restaurant-members/${memberId}`,
    );
  }
}