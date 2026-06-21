import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { AutoPart, SalesOrder, B2BAccount } from '../models/api';
import { statusColor, statusDot, categoryColor, categoryIcon, formatDate, formatCurrency } from '../shared/ui-helpers';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  parts = signal<AutoPart[]>([]);
  orders = signal<SalesOrder[]>([]);
  accounts = signal<B2BAccount[]>([]);
  searchQuery = signal('');
  selectedAccount = signal('');
  selectedPart = signal<AutoPart | null>(null);
  selectedOrder = signal<SalesOrder | null>(null);
  loadingParts = signal(false);
  loadingOrders = signal(false);

  totalParts = computed(() => this.parts().length);
  totalOrders = computed(() => this.orders().length);
  pendingOrders = computed(() => this.orders().filter(o => o.status === 'PENDING').length);
  deliveredOrders = computed(() => this.orders().filter(o => o.status === 'DELIVERED').length);

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadParts();
    this.loadOrders();
    this.api.getAccounts().subscribe(res => this.accounts.set(res.data));
  }

  loadParts(q?: string): void {
    this.loadingParts.set(true);
    this.api.getParts(q).subscribe({
      next: res => { this.parts.set(res.data.content); this.loadingParts.set(false); },
      error: () => this.loadingParts.set(false),
    });
  }

  loadOrders(): void {
    this.loadingOrders.set(true);
    const obs = this.selectedAccount()
      ? this.api.getOrders(this.selectedAccount())
      : this.api.getOrders();
    obs.subscribe({
      next: res => { this.orders.set(res.data.content); this.loadingOrders.set(false); },
      error: () => this.loadingOrders.set(false),
    });
  }

  onSearch(): void {
    this.loadParts(this.searchQuery() || undefined);
  }

  onAccountChange(): void {
    this.loadOrders();
  }

  viewPart(part: AutoPart): void {
    this.selectedPart.set(part);
  }

  closePartDetail(): void {
    this.selectedPart.set(null);
  }

  viewOrder(order: SalesOrder): void {
    this.selectedOrder.set(order);
  }

  closeOrderDetail(): void {
    this.selectedOrder.set(null);
  }

  statusColor = statusColor;
  statusDot = statusDot;
  categoryColor = categoryColor;
  categoryIcon = categoryIcon;
  formatDate = formatDate;
  formatCurrency = formatCurrency;
}
