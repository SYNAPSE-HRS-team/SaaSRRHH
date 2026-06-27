import { Empleado } from './empleado.model';

export interface EncuestaBienestar {
  idEncuesta?: number;
  fecha?: string;
  puntajeSatisfaccion: number;
  puntajeEstres: number;
  comentarios?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
