import { HttpClient } from '@angular/common/http';
import {
  Injectable,
  inject,
} from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { KitchenOrderItemReadDto } from '../../shared/models/kitchen.model';

@Injectable({
  providedIn: 'root',
})
export class Kitchen {
  private readonly http = inject(HttpClient);

  getActiveItems(
    restaurantId: string,
  ): Observable<KitchenOrderItemReadDto[]> {
    return this.http.get<KitchenOrderItemReadDto[]>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/kitchen/items`,
    );
  }

  startPreparing(
    restaurantId: string,
    orderItemId: string,
  ): Observable<KitchenOrderItemReadDto> {
    return this.http.post<KitchenOrderItemReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/kitchen/items/${orderItemId}/start`,
      {},
    );
  }

  markReady(
    restaurantId: string,
    orderItemId: string,
  ): Observable<KitchenOrderItemReadDto> {
    return this.http.post<KitchenOrderItemReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/kitchen/items/${orderItemId}/ready`,
      {},
    );
  }
}