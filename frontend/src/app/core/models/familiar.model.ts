import { Empleado } from './empleado.model';

export interface Familiar {
  idFamiliar?: number;
  nombre: string;
  parentesco: string;
  telefono?: string;
  correo?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
