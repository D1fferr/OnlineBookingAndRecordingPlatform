import { Component, inject, signal, effect } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { WorkingHoursService } from '../../../core/services/working-hours';
import { AuthService } from '../../../core/services/auth';
import { ListWorkingHoursCreateDTO, WorkingHoursCreateDTO } from '../../../core/models/working-hours';

export interface DayConfig {
  dayOfWeek: number;
  name: string;
}

@Component({
  selector: 'app-working-hours',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatSlideToggleModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './working-hours.html',
  styleUrl: './working-hours.css'
})
export class WorkingHoursComponent {
  private fb = inject(FormBuilder);
  private workingHoursService = inject(WorkingHoursService);
  private authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);

  providerId = signal<string | null>(this.authService.getProviderId());
  isLoading = signal<boolean>(false);

  days: DayConfig[] = [
    { dayOfWeek: 1, name: 'Monday' },
    { dayOfWeek: 2, name: 'Tuesday' },
    { dayOfWeek: 3, name: 'Wednesday' },
    { dayOfWeek: 4, name: 'Thursday' },
    { dayOfWeek: 5, name: 'Friday' },
    { dayOfWeek: 6, name: 'Saturday' },
    { dayOfWeek: 7, name: 'Sunday' }
  ];

  timeOptions: string[] = this.generateTimeOptions();
  sessionOptions: number[] = [15, 30, 45, 60, 90, 120];

  form: FormGroup = this.fb.group({
    daysFormArray: this.fb.array(this.days.map(day => this.createDayGroup(day.dayOfWeek)))
  });

  get daysFormArray(): FormArray {
    return this.form.get('daysFormArray') as FormArray;
  }

  constructor() {
    effect(() => {
      this.loadWorkingHours();
    });
  }

  private createDayGroup(dayOfWeek: number): FormGroup {
    return this.fb.group({
      dayOfWeek: [dayOfWeek],
      isActive: [true],
      startTime: ['09:00'],
      endTime: ['18:00'],
      breakStartTime: ['13:00'],
      breakEndTime: ['14:00'],
      sessionTime: [30]
    });
  }

  loadWorkingHours(): void {
    const currentProviderId = this.providerId();
    if (!currentProviderId) return;

    this.isLoading.set(true);
    this.workingHoursService.getWorkingHours(currentProviderId).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        if (response && response.workingHoursGetDTODTOList && response.workingHoursGetDTODTOList.length > 0) {
          const fetchedMap = new Map(response.workingHoursGetDTODTOList.map(item => [item.dayOfWeek, item]));

          this.days.forEach((day, index) => {
            const dto = fetchedMap.get(day.dayOfWeek);
            const group = this.daysFormArray.at(index) as FormGroup;

            if (dto) {
              group.patchValue({
                isActive: dto.isActive,
                startTime: this.normalizeTime(dto.startTime) || '09:00',
                endTime: this.normalizeTime(dto.endTime) || '18:00',
                breakStartTime: this.normalizeTime(dto.breakStartTime) || '13:00',
                breakEndTime: this.normalizeTime(dto.breakEndTime) || '14:00',
                sessionTime: dto.slotStep || 30
              });
            }
          });
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to load working hours', err);
      }
    });
  }

  saveSchedule(): void {
    const currentProviderId = this.providerId();
    if (!currentProviderId) {
      this.snackBar.open('Error: Provider ID is missing', 'Close', { duration: 3000 });
      return;
    }

    const rawList: WorkingHoursCreateDTO[] = this.daysFormArray.value.map((item: any) => ({
      dayOfWeek: item.dayOfWeek,
      isActive: item.isActive,
      startTime: item.isActive ? item.startTime : null,
      endTime: item.isActive ? item.endTime : null,
      breakStartTime: item.isActive ? item.breakStartTime : null,
      breakEndTime: item.isActive ? item.breakEndTime : null,
      sessionTime: item.isActive ? item.sessionTime : null
    }));

    const dto: ListWorkingHoursCreateDTO = {
      providerId: currentProviderId,
      workingHoursCreateDTOList: rawList
    };

    this.isLoading.set(true);
    this.workingHoursService.setWorkingHours(dto).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.snackBar.open('Working hours saved successfully!', 'Close', { duration: 3000 });
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to save schedule', err);
        this.snackBar.open('Failed to save working hours.', 'Close', { duration: 3000 });
      }
    });
  }

  private generateTimeOptions(): string[] {
    const times: string[] = [];
    for (let hour = 0; hour < 24; hour++) {
      for (let min of ['00', '30']) {
        const h = hour < 10 ? `0${hour}` : `${hour}`;
        times.push(`${h}:${min}`);
      }
    }
    return times;
  }

  private normalizeTime(timeStr?: string): string | null {
    if (!timeStr) return null;
    return timeStr.substring(0, 5);
  }
}
