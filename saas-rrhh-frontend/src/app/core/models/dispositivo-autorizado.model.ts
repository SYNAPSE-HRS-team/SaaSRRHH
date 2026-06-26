import { Empleado } from './empleado.model';

export interface DispositivoAutorizado {
  idDispositivo?: number;
  nombreDispositivo: string;
  direccionMac?: string;
  fechaRegistro?: string;
  estado?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
