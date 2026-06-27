import { Empleado } from './empleado.model';

export interface FeedbackAnonimo {
  idFeedback?: number;
  contenido: string;
  categoria?: string;
  fecha?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
