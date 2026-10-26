import {
  HttpClient,
  HttpParams,
} from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { CollectionRestDto } from '../../shared/models/collection-rest.model';
import {
  ScheduleShiftCreateDto,
  ScheduleShiftReadDto,
  ScheduleShiftUpdateDto,
} from '../../shared/models/schedule.model';

@Injectable({
  providedIn: 'root',
})
export class Schedule {
  private readonly http = inject(HttpClient);

  getPlannedByRestaurant(
    restaurantId: string,
  ): Observable<CollectionRestDto<ScheduleShiftReadDto>> {
    return this.http.get<
      CollectionRestDto<ScheduleShiftReadDto>
    >(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/schedule-shifts`,
    );
  }

  getByRestaurantAndPeriod(
    restaurantId: string,
    from: string,
    to: string,
  ): Observable<CollectionRestDto<ScheduleShiftReadDto>> {
    const params = new HttpParams()
      .set('from', from)
      .set('to', to);

    return this.http.get<
      CollectionRestDto<ScheduleShiftReadDto>
    >(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/schedule-shifts/period`,
      { params },
    );
  }

  getPlannedByEmployee(
    employeeId: string,
  ): Observable<CollectionRestDto<ScheduleShiftReadDto>> {
    return this.http.get<
      CollectionRestDto<ScheduleShiftReadDto>
    >(
      `${API_BASE_URL}/api/employees/${employeeId}/schedule-shifts`,
    );
  }

  getById(
    shiftId: string,
  ): Observable<ScheduleShiftReadDto> {
    return this.http.get<ScheduleShiftReadDto>(
      `${API_BASE_URL}/api/schedule-shifts/${shiftId}`,
    );
  }

  create(
    restaurantId: string,
    request: ScheduleShiftCreateDto,
  ): Observable<ScheduleShiftReadDto> {
    return this.http.post<ScheduleShiftReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/schedule-shifts`,
      request,
    );
  }

  update(
    shiftId: string,
    request: ScheduleShiftUpdateDto,
  ): Observable<ScheduleShiftReadDto> {
    return this.http.put<ScheduleShiftReadDto>(
      `${API_BASE_URL}/api/schedule-shifts/${shiftId}`,
      request,
    );
  }

  cancel(shiftId: string): Observable<void> {
    return this.http.patch<void>(
      `${API_BASE_URL}/api/schedule-shifts/${shiftId}/cancel`,
      {},
    );
  }

  complete(shiftId: string): Observable<void> {
    return this.http.patch<void>(
      `${API_BASE_URL}/api/schedule-shifts/${shiftId}/complete`,
      {},
    );
  }
}