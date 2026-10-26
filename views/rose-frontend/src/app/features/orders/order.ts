import { HttpClient } from '@angular/common/http';
import {
  Injectable,
  inject,
} from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import {
  OrderCreateDto,
  OrderItemCreateDto,
  OrderItemUpdateDto,
  OrderReadDto,
} from '../../shared/models/order.model';

@Injectable({
  providedIn: 'root',
})
export class Order {
  private readonly http = inject(HttpClient);

  getAllByRestaurant(
    restaurantId: string,
  ): Observable<OrderReadDto[]> {
    return this.http.get<OrderReadDto[]>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders`,
    );
  }

  getById(
    restaurantId: string,
    orderId: string,
  ): Observable<OrderReadDto> {
    return this.http.get<OrderReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders/${orderId}`,
    );
  }

  create(
    restaurantId: string,
    request: OrderCreateDto,
  ): Observable<OrderReadDto> {
    return this.http.post<OrderReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders`,
      request,
    );
  }

  addItem(
    restaurantId: string,
    orderId: string,
    request: OrderItemCreateDto,
  ): Observable<OrderReadDto> {
    return this.http.post<OrderReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders/${orderId}/items`,
      request,
    );
  }

  updateItem(
    restaurantId: string,
    orderId: string,
    orderItemId: string,
    request: OrderItemUpdateDto,
  ): Observable<OrderReadDto> {
    return this.http.put<OrderReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders/${orderId}/items/${orderItemId}`,
      request,
    );
  }

  removeItem(
    restaurantId: string,
    orderId: string,
    orderItemId: string,
  ): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders/${orderId}/items/${orderItemId}`,
    );
  }

  submit(
    restaurantId: string,
    orderId: string,
  ): Observable<OrderReadDto> {
    return this.http.post<OrderReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders/${orderId}/submit`,
      {},
    );
  }

  complete(
    restaurantId: string,
    orderId: string,
  ): Observable<OrderReadDto> {
    return this.http.post<OrderReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/orders/${orderId}/complete`,
      {},
    );
  }
}