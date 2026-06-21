import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { B2BAccount } from '../../models/api';
import { tierColor, formatCurrency } from '../../shared/ui-helpers';

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

  formatCurrency = formatCurrency;
  tierColor = tierColor;
}
