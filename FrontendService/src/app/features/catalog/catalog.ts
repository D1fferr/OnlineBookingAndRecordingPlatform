import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { CatalogService } from '../../core/services/catalog';
import { ProviderForGetClientRequestDTO } from '../../core/models/catalog';
import {MatDivider} from '@angular/material/divider';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDivider
  ],
  templateUrl: './catalog.html',
  styleUrl: './catalog.css'
})
export class CatalogComponent implements OnInit {
  private catalogService = inject(CatalogService);

  providers = signal<ProviderForGetClientRequestDTO[]>([]);
  categories = signal<string[]>([]);
  isLoading = signal<boolean>(false);

  searchQuery = signal<string>('');
  selectedCategory = signal<string | null>(null);
  showAllCategories = signal<boolean>(false);

  currentPage = signal<number>(0);
  pageSize = signal<number>(6);
  totalElements = signal<number>(0);

  ngOnInit(): void {
    this.loadCategories();
    this.loadProviders();
  }

  loadCategories(): void {
    this.catalogService.getCategories().subscribe({
      next: (res) => {
        if (res && res.categories) {
          this.categories.set(res.categories);
        }
      },
      error: (err) => console.error('Failed to load categories', err)
    });
  }

  loadProviders(): void {
    this.isLoading.set(true);

    const categoryParam = this.selectedCategory() || undefined;
    const searchParam = this.searchQuery() || undefined;

    this.catalogService.getProviders(
      this.currentPage(),
      this.pageSize(),
      categoryParam,
      searchParam
    ).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.providers.set(res.dtos || []);
        this.totalElements.set(res.totalElements || 0);
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to load providers', err);
      }
    });
  }

  onSearch(): void {
    this.currentPage.set(0);
    this.loadProviders();
  }

  selectCategory(category: string | null): void {
    if (this.selectedCategory() === category) {
      this.selectedCategory.set(null); // Скидання при повторному кліку
    } else {
      this.selectedCategory.set(category);
    }
    this.currentPage.set(0);
    this.loadProviders();
  }

  toggleCategories(): void {
    this.showAllCategories.update(v => !v);
  }

  onPageChange(event: PageEvent): void {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadProviders();
  }

  get visibleCategories(): string[] {
    if (this.showAllCategories()) {
      return this.categories();
    }
    return this.categories().slice(0, 5);
  }
}
