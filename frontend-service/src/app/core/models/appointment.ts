export type AppointmentsStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';
export interface AppointmentGetDTO {
  id: string;
  providerId: string;
  startTime: string;
  endTime: string;
  clientName: string;
  clientEmail: string;
  clientComment?: string;
  status: AppointmentsStatus;
}

export interface AppointmentPageDTO {
  dtoList: AppointmentGetDTO[];
  totalPages: number;
  totalElements: number;
}
export interface AppointmentCancelledReasonDTO {
  reason: string;
}
