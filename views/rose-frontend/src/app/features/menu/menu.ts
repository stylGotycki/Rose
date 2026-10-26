import { HttpClient } from '@angular/common/http';
import {
  Injectable,
  inject,
} from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../core/api/api.config';
import { CollectionRestDto } from '../../shared/models/collection-rest.model';
import {
  MenuCategoryCreateDto,
  MenuCategoryReadDto,
  MenuCategoryUpdateDto,
  MenuCreateDto,
  MenuItemCreateDto,
  MenuItemReadDto,
  MenuItemUpdateDto,
  MenuReadDto,
  MenuUpdateDto,
} from '../../shared/models/menu.model';

@Injectable({
  providedIn: 'root',
})
export class Menu {
  private readonly http = inject(HttpClient);

  getAllByRestaurant(
    restaurantId: string,
  ): Observable<CollectionRestDto<MenuReadDto>> {
    return this.http.get<
      CollectionRestDto<MenuReadDto>
    >(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/menus`,
    );
  }

  getById(
    menuId: string,
  ): Observable<MenuReadDto> {
    return this.http.get<MenuReadDto>(
      `${API_BASE_URL}/api/menus/${menuId}`,
    );
  }

  create(
    restaurantId: string,
    request: MenuCreateDto,
  ): Observable<MenuReadDto> {
    return this.http.post<MenuReadDto>(
      `${API_BASE_URL}/api/restaurants/${restaurantId}/menus`,
      request,
    );
  }

  update(
    menuId: string,
    request: MenuUpdateDto,
  ): Observable<MenuReadDto> {
    return this.http.put<MenuReadDto>(
      `${API_BASE_URL}/api/menus/${menuId}`,
      request,
    );
  }

  activate(
    menuId: string,
  ): Observable<MenuReadDto> {
    return this.http.patch<MenuReadDto>(
      `${API_BASE_URL}/api/menus/${menuId}/activate`,
      {},
    );
  }

  archive(
    menuId: string,
  ): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/menus/${menuId}`,
    );
  }

  getCategories(
    menuId: string,
  ): Observable<
    CollectionRestDto<MenuCategoryReadDto>
  > {
    return this.http.get<
      CollectionRestDto<MenuCategoryReadDto>
    >(
      `${API_BASE_URL}/api/menus/${menuId}/categories`,
    );
  }

  getCategoryById(
    categoryId: string,
  ): Observable<MenuCategoryReadDto> {
    return this.http.get<MenuCategoryReadDto>(
      `${API_BASE_URL}/api/menu-categories/${categoryId}`,
    );
  }

  createCategory(
    menuId: string,
    request: MenuCategoryCreateDto,
  ): Observable<MenuCategoryReadDto> {
    return this.http.post<MenuCategoryReadDto>(
      `${API_BASE_URL}/api/menus/${menuId}/categories`,
      request,
    );
  }

  updateCategory(
    categoryId: string,
    request: MenuCategoryUpdateDto,
  ): Observable<MenuCategoryReadDto> {
    return this.http.put<MenuCategoryReadDto>(
      `${API_BASE_URL}/api/menu-categories/${categoryId}`,
      request,
    );
  }

  archiveCategory(
    categoryId: string,
  ): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/menu-categories/${categoryId}`,
    );
  }

  getItems(
    categoryId: string,
  ): Observable<
    CollectionRestDto<MenuItemReadDto>
  > {
    return this.http.get<
      CollectionRestDto<MenuItemReadDto>
    >(
      `${API_BASE_URL}/api/menu-categories/${categoryId}/items`,
    );
  }

  getItemById(
    itemId: string,
  ): Observable<MenuItemReadDto> {
    return this.http.get<MenuItemReadDto>(
      `${API_BASE_URL}/api/menu-items/${itemId}`,
    );
  }

  createItem(
    categoryId: string,
    request: MenuItemCreateDto,
  ): Observable<MenuItemReadDto> {
    return this.http.post<MenuItemReadDto>(
      `${API_BASE_URL}/api/menu-categories/${categoryId}/items`,
      request,
    );
  }

  updateItem(
    itemId: string,
    request: MenuItemUpdateDto,
  ): Observable<MenuItemReadDto> {
    return this.http.put<MenuItemReadDto>(
      `${API_BASE_URL}/api/menu-items/${itemId}`,
      request,
    );
  }

  archiveItem(
    itemId: string,
  ): Observable<void> {
    return this.http.delete<void>(
      `${API_BASE_URL}/api/menu-items/${itemId}`,
    );
  }
}