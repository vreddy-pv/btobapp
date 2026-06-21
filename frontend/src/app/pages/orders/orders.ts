import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { SalesOrder } from '../../models/api';
import { statusColor, statusDot, formatDate, formatCurrency } from '../../shared/ui-helpers';

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

  statusColor = statusColor;
  statusDot = statusDot;
  formatDate = formatDate;
  formatCurrency = formatCurrency;
}
