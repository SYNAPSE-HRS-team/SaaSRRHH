import { Empleado } from './empleado.model';

export interface ReporteDiario {
  idReporte?: number;
  fecha: string;
  resumen: string;
  horasTrabajadas?: number;
  empleado?: Empleado;
  idEmpleado?: number;
}
