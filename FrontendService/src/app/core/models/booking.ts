export interface FreeSlotDTO {
  startTime: string; // ISO OffsetDateTime string
  endTime: string;   // ISO OffsetDateTime string
}

export interface DaySlotsDTO {
  dayOfWeek: number;
  date: string;
  freeSlots: FreeSlotDTO[];
}

export interface AvailableSlotsResponseDTO {
  timezone: string;
  appointments: DaySlotsDTO[];
}

export interface AppointmentCreateDTO {
  providerId: string;
  serviceId: string;
  startTime: string;
  endTime: string;
  clientName: string;
  clientEmail: string;
  clientComment: string;
}

export interface AppointmentGetForCreateDTO {
  service: string;
  providerName: string;
  startTime: string;
  endTime: string;
  price: number;
  clientName: string;
  clientEmail: string;
  clientComment: string;
}
