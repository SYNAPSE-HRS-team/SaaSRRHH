import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { HumanResourcesComponent } from './human-resources.component';
import { AttendanceDashboardService } from './attendance-dashboard.service';

describe('HumanResourcesComponent', () => {
  let component: HumanResourcesComponent;
  let fixture: ComponentFixture<HumanResourcesComponent>;
  let attendanceDashboardServiceSpy: jasmine.SpyObj<AttendanceDashboardService>;

  beforeEach(async () => {
    attendanceDashboardServiceSpy = jasmine.createSpyObj('AttendanceDashboardService', [
      'obtenerDashboard',
      'obtenerAsistenciasHoy',
      'obtenerIncidencias',
      'obtenerRankingTardanzas',
    ]);

    attendanceDashboardServiceSpy.obtenerDashboard.and.returnValue(of({
      totalEmpleados: 10,
      totalUsuarios: 12,
      reportesDiarios: 3,
      ausencias: 1,
      incidentes: 2,
      porcentajeAusentismo: 10,
      nivelRiesgo: 'BAJO',
    }));

    attendanceDashboardServiceSpy.obtenerAsistenciasHoy.and.returnValue(of([]));
    attendanceDashboardServiceSpy.obtenerIncidencias.and.returnValue(of([]));
    attendanceDashboardServiceSpy.obtenerRankingTardanzas.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      declarations: [HumanResourcesComponent],
      providers: [
        {
          provide: AttendanceDashboardService,
          useValue: attendanceDashboardServiceSpy,
        },
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HumanResourcesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load attendance dashboard data on init', () => {
    expect(attendanceDashboardServiceSpy.obtenerDashboard).toHaveBeenCalled();
    expect(component.dashboard.totalEmpleados).toBe(10);
    expect(component.tarjetasResumen.length).toBe(4);
  });
});
