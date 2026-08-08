import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

import { CatalogService } from '../../core/services/catalog';
import {ProviderForGetBookingRequestDTO, ServiceGetDTO, WorkingHoursGetDTO} from '../../core/models/catalog';

@Component({
  selector: 'app-provider-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './provider-detail.html',
  styleUrl: './provider-detail.css'
})
export class ProviderDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private catalogService = inject(CatalogService);

  provider = signal<ProviderForGetBookingRequestDTO | null>(null);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  private readonly daysMap: { [key: number]: string } = {
    1: 'Monday',
    2: 'Tuesday',
    3: 'Wednesday',
    4: 'Thursday',
    5: 'Friday',
    6: 'Saturday',
    7: 'Sunday'
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadProvider(id);
    } else {
      this.errorMessage.set('Provider ID is missing.');
      this.isLoading.set(false);
    }
  }

  loadProvider(id: string): void {
    this.isLoading.set(true);
    this.catalogService.getProviderById(id).subscribe({
      next: (data) => {
        this.provider.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load provider details', err);
        this.errorMessage.set('Failed to load master details. Please try again later.');
        this.isLoading.set(false);
      }
    });
  }

  goBack(): void {
    this.location.back();
  }

  getDayName(dayOfWeek: number): string {
    return this.daysMap[dayOfWeek] || `Day ${dayOfWeek}`;
  }

  formatTime(timeStr?: string): string {
    if (!timeStr) return '';
    return timeStr.substring(0, 5);
  }


  bookService(service: ServiceGetDTO): void {
    this.router.navigate(['/booking', service.id], {
      state: {
        service: service,
        provider: this.provider()
      }
    });
  }
}
