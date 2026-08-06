export interface WorkingHoursGetDTO {
  id?: string;
  dayOfWeek: number;
  startTime?: string;
  endTime?: string;
  breakStartTime?: string;
  breakEndTime?: string;
  slotStep?: number;
  isActive: boolean;
}

export interface ListWorkingHoursGetDTO {
  workingHoursGetDTODTOList: WorkingHoursGetDTO[];
  providerId: string;
}

export interface WorkingHoursCreateDTO {
  dayOfWeek: number;
  startTime?: string;
  endTime?: string;
  breakStartTime?: string;
  breakEndTime?: string;
  slotStep?: number;
  isActive: boolean;
}

export interface ListWorkingHoursCreateDTO {
  workingHoursCreateDTOList: WorkingHoursCreateDTO[];
  providerId: string;
}
