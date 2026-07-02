import { Empleado } from './empleado.model';

export interface BoletaPago {
  id?: number;
  empleadoId: number;
  empleadoNombre?: string;
  planillaId?: number;
  planillaMes?: number;
  planillaAnio?: number;
  sueldoBase: number;
  diasTrabajados: number;
  diasNoTrabajados: number;
  asignacionFamiliar: number;
  bonoBeta: number;
  horasExtraPago: number;
  otrosBonos: number;
  descuentoInasistencia: number;
  otrosDescuentos: number;
  totalIngresos: number;
  totalDescuentos: number;
  netoPagar: number;
  fechaEmision?: string;
  pdfUrl?: string;
  // Opcional: empleado embebido para mostrar en UI
  empleado?: Empleado;
}

export interface BonoDescuento {
  id?: number;
  empleadoId: number;
  tipo: 'BONO' | 'DESCUENTO';
  monto: number;
  descripcion: string;
  mes: number;
  anio: number;
}
