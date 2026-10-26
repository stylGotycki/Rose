export type MenuStatus =
  | 'DRAFT'
  | 'ACTIVE'
  | 'ARCHIVED';

export type MenuCategoryStatus =
  | 'ACTIVE'
  | 'ARCHIVED';

export type MenuItemStatus =
  | 'ACTIVE'
  | 'ARCHIVED';

export interface MenuReadDto {
  id: string;
  restaurantId: string;
  name: string;
  description?: string | null;
  status: MenuStatus;
  createdAt: string;
  updatedAt: string;
}

export interface MenuCreateDto {
  name: string;
  description?: string | null;
}

export interface MenuUpdateDto {
  name: string;
  description?: string | null;
}

export interface MenuCategoryReadDto {
  id: string;
  menuId: string;
  name: string;
  displayOrder: number;
  status: MenuCategoryStatus;
  createdAt: string;
  updatedAt: string;
}

export interface MenuCategoryCreateDto {
  name: string;
  displayOrder: number;
}

export interface MenuCategoryUpdateDto {
  name: string;
  displayOrder: number;
}

export interface MenuItemReadDto {
  id: string;
  categoryId: string;
  name: string;
  description?: string | null;
  price: number;
  currency: string;
  available: boolean;
  status: MenuItemStatus;
  createdAt: string;
  updatedAt: string;
}

export interface MenuItemCreateDto {
  name: string;
  description?: string | null;
  price: number;
  currency: string;
  available: boolean;
}

export interface MenuItemUpdateDto {
  name: string;
  description?: string | null;
  price: number;
  currency: string;
  available: boolean;
}