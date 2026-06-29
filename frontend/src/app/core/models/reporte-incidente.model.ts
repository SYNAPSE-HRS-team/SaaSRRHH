export interface ReporteIncidente {
  id: number;
  empleadoId: number;
  supervisorId?: number;
  tareaId?: number;
  areaId?: number;
  tipo: TipoIncidente;
  descripcion: string;
  evidenciaUrl?: string;
  nivelRiesgo: NivelRiesgo;
  estado: EstadoIncidente;
  fechaIncidente: Date;
  fechaRegistro: Date;
}

export enum TipoIncidente {
  ACTO_SEGURO = 'ACTO_SEGURO',
  ACTO_INSEGURO = 'ACTO_INSEGURO',
  INCIDENTE = 'INCIDENTE',
  ACCIDENTE = 'ACCIDENTE',
}

export enum NivelRiesgo {
  BAJO = 'BAJO',
  MEDIO = 'MEDIO',
  ALTO = 'ALTO',
  CRITICO = 'CRITICO',
}

export enum EstadoIncidente {
  REPORTADO = 'REPORTADO',
  EN_REVISION = 'EN_REVISION',
  CERRADO = 'CERRADO',
}

export interface ReporteIncidenteRequest {
  empleadoId: number;
  supervisorId?: number;
  tareaId?: number;
  areaId?: number;
  tipo: string;
  descripcion: string;
  evidenciaUrl?: string;
  nivelRiesgo: string;
  estado: string;
  fechaIncidente: Date;
}

export interface ReporteIncidenteResponse {
  id: number;
  empleadoId: number;
  supervisorId?: number;
  tareaId?: number;
  areaId?: number;
  tipo: string;
  descripcion: string;
  evidenciaUrl?: string;
  nivelRiesgo: string;
  estado: string;
  fechaIncidente: Date;
  fechaRegistro: Date;
}
