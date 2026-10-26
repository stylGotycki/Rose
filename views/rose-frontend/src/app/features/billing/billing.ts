import { HttpClient } from '@angular/common/http';
import {
  Injectable,
  inject,
} from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { BillReadDto } from '../../shared/models/billing.model';

@Injectable({
  providedIn: 'root',
})
export class Billing {
  private readonly http = inject(HttpClient);

  getAll(
    restaurantId: string,
  ): Observable<BillReadDto[]> {
    return this.http.get<BillReadDto[]>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/bills`,
    );
  }

  getById(
    restaurantId: string,
    billId: string,
  ): Observable<BillReadDto> {
    return this.http.get<BillReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/bills/${billId}`,
    );
  }

  getByOrderId(
    restaurantId: string,
    orderId: string,
  ): Observable<BillReadDto> {
    return this.http.get<BillReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders/${orderId}/bill`,
    );
  }

  pay(
    restaurantId: string,
    billId: string,
  ): Observable<BillReadDto> {
    return this.http.post<BillReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/bills/${billId}/pay`,
      {},
    );
  }
}