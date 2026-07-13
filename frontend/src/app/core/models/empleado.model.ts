export interface EmpleadoRequest {
  usuarioId?: number;
  nombres: string;
  apellidos: string;
  dni: string;
  fotoPerfilUrl?: string;
  sueldoBase?: number;
  asignacionFamiliar?: boolean;
  fechaInicioContrato?: string;
  fechaFinContrato?: string;
  cargo?: string;
  activo?: boolean;
  // ✅ NUEVOS CAMPOS: HORARIO LABORAL
  horaEntrada?: string;
  horaSalida?: string;
  diasLaborables?: string;
  toleranciaMinutos?: number;
  // ✅ NUEVOS CAMPOS: TIPO DE PAGO
  tipoPago?: string;
  montoPago?: number;
}

export interface EmpleadoResponse {
  id: number;
  usuarioId?: number;
  email?: string;
  nombres: string;
  apellidos: string;
  dni: string;
  fotoPerfilUrl?: string;
  sueldoBase?: number;
  asignacionFamiliar?: boolean;
  fechaInicioContrato?: string;
  fechaFinContrato?: string;
  cargo?: string;
  activo?: boolean;
  fechaRegistro?: string;
  // ✅ NUEVOS CAMPOS: HORARIO LABORAL
  horaEntrada?: string;
  horaSalida?: string;
  diasLaborables?: string;
  toleranciaMinutos?: number;
  // ✅ NUEVOS CAMPOS: TIPO DE PAGO
  tipoPago?: string;
  montoPago?: number;
  // ✅ NUEVOS CAMPOS: MÉTRICAS
  ultimoNivelRiesgo?: string;
  indicePuntualidad?: number;
  totalFaltasMes?: number;
  totalTardanzasMes?: number;
}

export type Empleado = EmpleadoResponse;