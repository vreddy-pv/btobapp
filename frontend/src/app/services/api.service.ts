import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, AutoPart, Page, SalesOrder, B2BAccount } from '../models/api';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  getParts(q?: string, category?: string, page = 0, size = 20): Observable<ApiResponse<Page<AutoPart>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (q) params = params.set('q', q);
    if (category) params = params.set('category', category);
    return this.http.get<ApiResponse<Page<AutoPart>>>(`${this.base}/catalog/parts`, { params });
  }

  getPart(sku: string): Observable<ApiResponse<AutoPart>> {
    return this.http.get<ApiResponse<AutoPart>>(`${this.base}/catalog/parts/${sku}`);
  }

  getOrders(accountNumber?: string, page = 0, size = 20): Observable<ApiResponse<Page<SalesOrder>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (accountNumber) params = params.set('accountNumber', accountNumber);
    return this.http.get<ApiResponse<Page<SalesOrder>>>(`${this.base}/orders`, { params });
  }

  getOrder(orderNumber: string): Observable<ApiResponse<SalesOrder>> {
    return this.http.get<ApiResponse<SalesOrder>>(`${this.base}/orders/${orderNumber}`);
  }

  createOrder(accountNumber: string, items: { sku: string; quantity: number }[]): Observable<ApiResponse<SalesOrder>> {
    return this.http.post<ApiResponse<SalesOrder>>(`${this.base}/orders`, { accountNumber, items });
  }

  getAccounts(): Observable<ApiResponse<B2BAccount[]>> {
    return this.http.get<ApiResponse<B2BAccount[]>>(`${this.base}/accounts`);
  }
}
