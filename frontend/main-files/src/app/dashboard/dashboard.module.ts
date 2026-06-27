import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { HighchartsChartModule } from 'highcharts-angular';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';

import { DefaultComponent } from './default/default.component';
import { ECommerceComponent } from './e-commerce/e-commerce.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { DigitalMarketingComponent } from './digital-marketing/digital-marketing.component';
import { HumanResourcesComponent } from './human-resources/human-resources.component';
import { EmployeeAttendanceComponent } from './employee-attendance/employee-attendance.component';


@NgModule({
  declarations: [
     DefaultComponent, ECommerceComponent, AnalyticsComponent, DigitalMarketingComponent, HumanResourcesComponent, EmployeeAttendanceComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    DashboardRoutingModule,
    PerfectScrollbarModule,
    HighchartsChartModule
  ]
})
export class DashboardModule { }
