export type ShiftPosition =
  | 'MANAGER'
  | 'CHEF'
  | 'WAITER'
  | 'BARTENDER'
  | 'CASHIER'
  | 'CLEANER'
  | 'DELIVERY'
  | 'STAFF';

export type ScheduleShiftStatus =
  | 'PLANNED'
  | 'CANCELLED'
  | 'COMPLETED';

export interface ScheduleShiftReadDto {
  id: string;
  restaurantId: string;
  employeeId: string;
  startsAt: string;
  endsAt: string;
  position: ShiftPosition;
  status: ScheduleShiftStatus;
  note?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ScheduleShiftCreateDto {
  employeeId: string;
  startsAt: string;
  endsAt: string;
  position: ShiftPosition;
  note?: string | null;
}

export interface ScheduleShiftUpdateDto {
  startsAt: string;
  endsAt: string;
  position: ShiftPosition;
  note?: string | null;
}