export interface MetricaBurnoutResponse {
  id: number;
  empleadoId: number;
  nombreEmpleado?: string;
  nivelRiesgo: 'BAJO' | 'MEDIO' | 'ALTO';
  horasExtraAcumuladas: number;
  tendenciaTardanza: boolean;
  fechaEvaluacion: string;
  recomendaciones?: string;
  // ✅ NUEVOS CAMPOS
  faltasPeriodo?: number;
  tardanzasPeriodo?: number;
  patronDetectado?: string;
  indicePuntualidad?: number;
  diasTrabajados?: number;
  horasReales?: number;
  horasContrato?: number;
  cargo?: string;
  dniEmpleado?: string;
  fechaInicioContrato?: string;
}

export interface MetricaBurnoutRequest {
  empleadoId: number;
  nivelRiesgo: 'BAJO' | 'MEDIO' | 'ALTO';
  horasExtraAcumuladas: number;
  tendenciaTardanza: boolean;
  // ✅ NUEVOS CAMPOS
  faltasPeriodo?: number;
  tardanzasPeriodo?: number;
  patronDetectado?: string;
  indicePuntualidad?: number;
  diasTrabajados?: number;
  horasReales?: number;
  horasContrato?: number;
}