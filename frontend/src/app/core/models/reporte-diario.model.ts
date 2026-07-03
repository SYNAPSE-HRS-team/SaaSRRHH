export interface ReporteDiarioResponse {
  id: number;
  tareaId: number;
  empleadoId: number;
  descripcionTrabajador: string;
  observacionSupervisor?: string;
  porcentajeAvance: number;
  estado: 'PENDIENTE' | 'VALIDADO' | 'OBSERVADO';
  fechaReporte: string;
}

export interface ReporteDiarioRequest {
  id?: number;
  tareaId: number;
  empleadoId: number;
  descripcionTrabajador: string;
  observacionSupervisor?: string;
  porcentajeAvance: number;
  estado: 'PENDIENTE' | 'VALIDADO' | 'OBSERVADO';
}
