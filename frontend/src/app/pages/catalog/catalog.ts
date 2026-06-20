import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AutoPart } from '../../models/api';

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

  formatCurrency(amount: number): string {
    return '$' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  categoryColor(category: string): string {
    const map: Record<string, string> = {
      Brakes: 'bg-red-50 text-red-500',
      Filters: 'bg-blue-50 text-blue-500',
      Electrical: 'bg-amber-50 text-amber-500',
      Engine: 'bg-violet-50 text-violet-500',
      Suspension: 'bg-emerald-50 text-emerald-500',
      Belts: 'bg-orange-50 text-orange-500',
    };
    return map[category] || 'bg-slate-50 text-slate-400';
  }

  categoryIcon(category: string): string {
    const map: Record<string, string> = {
      Brakes: 'M12 8c-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm0 6c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zM21 12c0 4.97-4.03 9-9 9s-9-4.03-9-9 4.03-9 9-9 9 4.03 9 9z',
      Filters: 'M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z',
      Electrical: 'M13 10V3L4 14h7v7l9-11h-7z',
      Engine: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z',
      Suspension: 'M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15',
      Belts: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
    };
    return map[category] || 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4';
  }
}
