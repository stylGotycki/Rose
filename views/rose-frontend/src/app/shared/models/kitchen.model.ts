export type KitchenItemStatus =
  | 'PENDING'
  | 'IN_PREPARATION'
  | 'READY';

export interface KitchenOrderItemReadDto {
  id: string;
  orderId: string;
  menuItemId: string;
  name: string;
  quantity: number;
  note?: string | null;
  status: KitchenItemStatus;
  createdAt: string;
  updatedAt: string;
}