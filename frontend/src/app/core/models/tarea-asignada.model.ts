import { Empleado } from './empleado.model';

export interface TareaAsignada {
  idTarea?: number;
  titulo: string;
  descripcion?: string;
  fechaAsignacion?: string;
  fechaVencimiento?: string;
  estado?: string;
  prioridad?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
