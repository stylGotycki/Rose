import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { CollectionRestDto } from '../../shared/models/collection-rest.model';
import {
  EmployeeCreateDto,
  EmployeeReadDto,
  EmployeeUpdateDto,
} from '../../shared/models/employee.model';

@Injectable({
  providedIn: 'root',
})
export class Employee {
  private readonly http = inject(HttpClient);

  getAllByRestaurant(
    restaurantId: string,
  ): Observable<CollectionRestDto<EmployeeReadDto>> {
    return this.http.get<CollectionRestDto<EmployeeReadDto>>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/employees`,
    );
  }

  getById(employeeId: string): Observable<EmployeeReadDto> {
    return this.http.get<EmployeeReadDto>(
      `${API_BASE_URL}/api/employees/${employeeId}`,
    );
  }

  create(
    restaurantId: string,
    request: EmployeeCreateDto,
  ): Observable<EmployeeReadDto> {
    return this.http.post<EmployeeReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/employees`,
      request,
    );
  }

  update(
    employeeId: string,
    request: EmployeeUpdateDto,
  ): Observable<EmployeeReadDto> {
    return this.http.put<EmployeeReadDto>(
      `${API_BASE_URL}/api/employees/${employeeId}`,
      request,
    );
  }

  archive(employeeId: string): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/employees/${employeeId}`,
    );
  }
}