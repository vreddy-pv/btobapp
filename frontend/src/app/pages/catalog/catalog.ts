import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AutoPart } from '../../models/api';
import { categoryColor, categoryIcon, formatCurrency } from '../../shared/ui-helpers';

@Component({
  selector: 'app-catalog',
  imports: [CommonModule, FormsModule],
  templateUrl: './catalog.html',
})
export class Catalog implements OnInit {
  parts = signal<AutoPart[]>([]);
  searchQuery = signal('');
  selectedPart = signal<AutoPart | null>(null);
  loading = signal(false);

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadParts();
  }

  loadParts(q?: string): void {
    this.loading.set(true);
    this.api.getParts(q).subscribe({
      next: res => { this.parts.set(res.data.content); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  onSearch(): void {
    this.loadParts(this.searchQuery() || undefined);
  }

  viewPart(part: AutoPart): void {
    this.selectedPart.set(part);
  }

  closePartDetail(): void {
    this.selectedPart.set(null);
  }

  formatCurrency = formatCurrency;
  categoryColor = categoryColor;
  categoryIcon = categoryIcon;
}
