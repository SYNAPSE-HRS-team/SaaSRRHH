import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/guards/auth.guard';

export const FEEDBACK_ROUTES: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    loadComponent: () => import('./page/feedback-list/feedback-list.component').then(m => m.FeedbackListComponent),
    title: 'Feedback - SaaSRRHH'
  }
];