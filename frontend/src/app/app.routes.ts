import { Routes } from '@angular/router';
import { authGuard } from './features/auth/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'apply' },
  {
    path: 'apply',
    loadComponent: () => import('./features/eligibility/student-form').then((m) => m.StudentForm)
  },
  {
    path: 'result/:id',
    loadComponent: () => import('./features/eligibility/result-page').then((m) => m.ResultPage)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login').then((m) => m.Login)
  },
  {
    path: 'history',
    canActivate: [authGuard],
    loadComponent: () => import('./features/history/history-page').then((m) => m.HistoryPage)
  },
  {
    path: 'statistics',
    canActivate: [authGuard],
    loadComponent: () => import('./features/statistics/statistics-page').then((m) => m.StatisticsPage)
  },
  {
    path: '**',
    loadComponent: () => import('./core/not-found/not-found').then((m) => m.NotFound)
  }
];
