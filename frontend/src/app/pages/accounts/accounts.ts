import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { B2BAccount } from '../../models/api';

@Component({
  selector: 'app-accounts',
  imports: [CommonModule],
  templateUrl: './accounts.html',
})
export class Accounts implements OnInit {
  accounts = signal<B2BAccount[]>([]);
  loading = signal(false);

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading.set(true);
    this.api.getAccounts().subscribe({
      next: res => { this.accounts.set(res.data); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  formatCurrency(amount: number): string {
    return '$' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  tierColor(tier: string): string {
    const map: Record<string, string> = {
      PLATINUM: 'text-violet-600 bg-violet-50 border-violet-200',
      GOLD: 'text-amber-600 bg-amber-50 border-amber-200',
      SILVER: 'text-slate-600 bg-slate-100 border-slate-200',
      BRONZE: 'text-orange-600 bg-orange-50 border-orange-200',
    };
    return map[tier] || 'text-slate-600 bg-slate-50 border-slate-200';
  }
}
