import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DefaultComponent } from './default/default.component';
import { ECommerceComponent } from './e-commerce/e-commerce.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { DigitalMarketingComponent } from './digital-marketing/digital-marketing.component';
import { HumanResourcesComponent } from './human-resources/human-resources.component';
import { EmployeeAttendanceComponent } from './employee-attendance/employee-attendance.component';
import { AuthGuard } from '../guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    children: [
      { path: 'default', component: DefaultComponent, data: { title: 'Default' } },
      { path: 'e-commerce', component: ECommerceComponent, data: { title: 'e-Commerce' } },
      { path: 'analytics', component: AnalyticsComponent, data: { title: 'Analytics' } },
      { path: 'digital-marketing', component: DigitalMarketingComponent, data: { title: 'Digital Marketing' } },
      {
        path: 'human-resources',
        component: HumanResourcesComponent,
        canActivate: [AuthGuard],
        data: { title: 'Asistencias', roles: ['ADMIN', 'SUPERVISOR'] },
      },
      {
        path: 'mi-asistencia',
        component: EmployeeAttendanceComponent,
        canActivate: [AuthGuard],
        data: { title: 'Mi asistencia', roles: ['EMPLEADO', 'OBRERO'] },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule { }
