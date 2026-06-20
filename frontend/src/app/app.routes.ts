import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { Catalog } from './pages/catalog/catalog';
import { Orders } from './pages/orders/orders';
import { Accounts } from './pages/accounts/accounts';

export const routes: Routes = [
  { path: '', component: Dashboard },
  { path: 'catalog', component: Catalog },
  { path: 'orders', component: Orders },
  { path: 'accounts', component: Accounts },
];
