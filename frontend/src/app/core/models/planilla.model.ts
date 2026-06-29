export type EstadoPlanilla = 'PROCESADO' | 'CERRADO';

export interface Planilla {
  id?: number;
  mes: number;
  anio: number;
  totalPagado?: number;
  estado?: EstadoPlanilla;
  fechaCierre?: string;
}

export interface PlanillaRequest {
  mes: number;
  anio: number;
  totalPagado?: number;
  estado?: EstadoPlanilla;
  fechaCierre?: string;
}

export const MESES_NOMBRE: string[] = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
];

export function getMesNombre(mes: number): string {
  return MESES_NOMBRE[mes - 1] ?? `Mes ${mes}`;
}
