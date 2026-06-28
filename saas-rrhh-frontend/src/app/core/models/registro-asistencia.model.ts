import { Empleado } from './empleado.model';

export interface RegistroAsistencia {
  idAsistencia?: number;
  entrada: string;
  salida?: string;
  fecha: string;
  estado?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
