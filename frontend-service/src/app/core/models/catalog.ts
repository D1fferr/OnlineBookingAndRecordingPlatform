export interface ServiceProviderForGetClientRequestDTO {
  serviceName: string;
  duration: number;
  price: number;
}

export interface ProviderForGetClientRequestDTO {
  id: string;
  name: string;
  serviceType: string;
  timezone: string;
  avatarURL?: string;
  serviceProviders: ServiceProviderForGetClientRequestDTO[];
}

export interface ProviderPageForGetClientRequestDTO {
  dtos: ProviderForGetClientRequestDTO[];
  totalPages: number;
  totalElements: number;
}

export interface ProviderListServiceTypeDTO {
  categories: string[];
}
export interface WorkingHoursGetDTO {
  id: string;
  dayOfWeek: number;
  startTime: string;
  endTime: string;
  breakStartTime?: string;
  breakEndTime?: string;
  slotStep?: number;
  isActive: boolean;
}

export interface ServiceGetDTO {
  id: string;
  serviceName: string;
  duration: number;
  price: number;
  description: string;
}

export interface ProviderForGetBookingRequestDTO {
  id: string;
  name: string;
  serviceType: string;
  timezone: string;
  avatarURL?: string;
  workingHours: WorkingHoursGetDTO[];
  serviceProviders: ServiceGetDTO[];
}
