import { Component, inject, signal, effect } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

import { ServiceGetDTO, ServiceCreateDTO, ServiceUpdateDTO } from '../../../../core/models/service';
import { ServiceManagementService } from '../../../../core/services/service-management';
import { AuthService } from '../../../../core/services/auth';
import { ServiceDialogComponent } from '../service-dialog/service-dialog';

@Component({
  selector: 'app-services',
  standalone: true,
  imports: [
    CurrencyPipe,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatPaginatorModule
  ],
  templateUrl: './services.html',
  styleUrl: './services.css'
})
export class ServicesComponent {
  private serviceService = inject(ServiceManagementService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = ['serviceName', 'duration', 'price', 'description', 'actions'];

  providerId = signal<string | null>(this.authService.getProviderId());

  pageIndex = signal<number>(0);
  servicePerPage = signal<number>(8);
  totalElements = signal<number>(0);

  services = signal<ServiceGetDTO[]>([]);

  constructor() {
    effect(() => {
      this.loadServices();
    });
  }

  loadServices(): void {
    const currentProviderId = this.providerId();
    if (!currentProviderId) {
      console.warn('Provider ID is not available in JWT token');
      return;
    }

    this.serviceService.getServices(
      currentProviderId,
      this.pageIndex(),
      this.servicePerPage()
    ).subscribe({
      next: (response) => {
        this.services.set(response.dtos);
        this.totalElements.set(response.totalElements);
      },
      error: (err) => console.error('Failed to load services', err)
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.servicePerPage.set(event.pageSize);
  }

  openAddDialog(): void {
    const currentProviderId = this.providerId();
    if (!currentProviderId) return;

    const dialogRef = this.dialog.open(ServiceDialogComponent, {
      width: '450px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const createDto: ServiceCreateDTO = {
          providerId: currentProviderId,
          ...result
        };
        this.serviceService.createService(createDto).subscribe({
          next: () => this.loadServices(),
          error: (err) => console.error('Failed to create service', err)
        });
      }
    });
  }

  openEditDialog(service: ServiceGetDTO): void {
    const dialogRef = this.dialog.open(ServiceDialogComponent, {
      width: '450px',
      data: { service }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const updateDto: ServiceUpdateDTO = result;
        this.serviceService.updateService(service.id, updateDto).subscribe({
          next: () => this.loadServices(),
          error: (err) => console.error('Failed to update service', err)
        });
      }
    });
  }

  deleteService(id: string): void {
    if (confirm('Are you sure you want to delete this service?')) {
      this.serviceService.deleteService(id).subscribe({
        next: () => this.loadServices(),
        error: (err) => console.error('Failed to delete service', err)
      });
    }
  }
}
