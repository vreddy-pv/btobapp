import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { SalesOrder } from '../../models/api';

@Component({
  selector: 'app-orders',
  imports: [CommonModule],
  templateUrl: './orders.html',
})
export class Orders implements OnInit {
  orders = signal<SalesOrder[]>([]);
  selectedOrder = signal<SalesOrder | null>(null);
  loading = signal(false);

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true);
    this.api.getOrders().subscribe({
      next: res => { this.orders.set(res.data.content); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  viewOrder(order: SalesOrder): void {
    this.selectedOrder.set(order);
  }

  closeOrderDetail(): void {
    this.selectedOrder.set(null);
  }

  statusColor(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'text-amber-600 bg-amber-50 border-amber-200',
      SHIPPED: 'text-blue-600 bg-blue-50 border-blue-200',
      DELIVERED: 'text-emerald-600 bg-emerald-50 border-emerald-200',
      CANCELLED: 'text-red-600 bg-red-50 border-red-200',
    };
    return map[status] || 'text-slate-600 bg-slate-50 border-slate-200';
  }

  statusDot(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'bg-amber-500',
      SHIPPED: 'bg-blue-500',
      DELIVERED: 'bg-emerald-500',
      CANCELLED: 'bg-red-500',
    };
    return map[status] || 'bg-slate-500';
  }

  formatDate(date: string): string {
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }

  formatCurrency(amount: number): string {
    return '$' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }
}
