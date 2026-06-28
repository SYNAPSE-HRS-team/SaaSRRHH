import { CommonModule } from '@angular/common'; // ✅ IMPORTANTE
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // ✅ IMPORTANTE
import { RouterModule } from '@angular/router';

import { REPORTE_INCIDENTE_ROUTES } from './reporte-incidente.routes';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(REPORTE_INCIDENTE_ROUTES),
  ],
})
export class ReporteIncidenteModule {}
