export interface RegistroAsistencia {
  id?: number;
  idAsistencia?: number;
  empleadoId?: number;
  dispositivoId?: number;
  fechaHora?: string;
  tipoMarcacion?: 'ENTRADA' | 'SALIDA' | string;
  metodo?: string;
  estado?: string;
  observaciones?: string;
}
export interface AsistenciaQr {
  payload: string;
  empleadoId: number;
  empleadoNombre: string;
  segundosRestantes: number;
  expiraEnEpoch: number;
}
export interface CalendarioDia {
  fecha: string;
  estado: 'ASISTIO' | 'FALTA' | 'NEUTRO' | string;
  asistenciaId?: number | null;
  horaEntrada?: string | null;
  horaSalida?: string | null;
}
export interface CalendarioMes {
  anio: number;
  mes: number;
  dias: CalendarioDia[];
}
export interface CalendarioAnual {
  anio: number;
  meses: CalendarioMes[];
}