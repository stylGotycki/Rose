export type BillStatus =
  | 'OPEN'
  | 'PAID';

export interface BillReadDto {
  id: string;
  orderId: string;
  restaurantId: string;
  status: BillStatus;
  totalAmount: number;
  currency: string;
  createdAt: string;
  updatedAt: string;
  paidAt?: string | null;
}