import { Empleado } from './empleado.model';

export interface BoletaPago {
  idBoleta?: number;
  periodo: string;
  monto: number;
  descuentos?: number;
  bonificaciones?: number;
  totalPagar: number;
  fechaEmision?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
