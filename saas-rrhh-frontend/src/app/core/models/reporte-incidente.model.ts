import { Empleado } from './empleado.model';

export interface ReporteIncidente {
  idIncidente?: number;
  titulo: string;
  descripcion: string;
  fechaReporte?: string;
  estado?: string;
  prioridad?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
