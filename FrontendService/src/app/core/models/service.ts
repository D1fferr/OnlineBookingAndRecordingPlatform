export interface ServiceGetDTO {
  id: string;
  serviceName: string;
  duration: number;
  price: number;
  description: string;
}

export interface ServicePageDTO {
  dtos: ServiceGetDTO[];
  providerId: string;
  totalPages: number;
  totalElements: number;
}

export interface ServiceCreateDTO {
  providerId: string;
  serviceName: string;
  duration: number;
  price: number;
  description: string;
}

export interface ServiceUpdateDTO {
  serviceName: string;
  duration: number;
  price: number;
  description: string;
}
