import { Empleado } from './empleado.model';

export interface MetricaBurnout {
  idMetrica?: number;
  fecha?: string;
  puntajeBurnout: number;
  nivelRiesgo?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}
