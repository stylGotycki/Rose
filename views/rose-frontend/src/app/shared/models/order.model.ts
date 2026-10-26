export type OrderStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'IN_PREPARATION'
  | 'READY'
  | 'COMPLETED';

export type OrderItemStatus =
  | 'PENDING'
  | 'IN_PREPARATION'
  | 'READY'
  | 'SERVED';

export interface OrderItemReadDto {
  id: string;
  menuItemId: string;
  name: string;
  unitPrice: number;
  currency: string;
  quantity: number;
  status: OrderItemStatus;
  note?: string | null;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
}

export interface OrderReadDto {
  id: string;
  restaurantId: string;
  createdByUserId: string;
  status: OrderStatus;
  note?: string | null;
  items: OrderItemReadDto[];
  totalAmount: number;
  currency?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface OrderCreateDto {
  note?: string | null;
}

export interface OrderItemCreateDto {
  menuItemId: string;
  quantity: number;
  note?: string | null;
}

export interface OrderItemUpdateDto {
  quantity: number;
  note?: string | null;
}